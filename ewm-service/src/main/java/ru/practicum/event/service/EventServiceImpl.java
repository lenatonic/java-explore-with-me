package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.error.exceptions.NotFoundException;
import ru.practicum.error.exceptions.NotValidException;
import ru.practicum.error.exceptions.WrongEventDateException;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.location.Location;
import ru.practicum.location.LocationMapper;
import ru.practicum.location.LocationRepository;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.util.Patterns;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final StatsClient statsClient;

    @Override
    @Transactional
    public EventFullDto addEvent(NewEventDto newEventDto, Long id) {
        if (newEventDto.getEventDate() != null && !LocalDateTime
                .parse(newEventDto.getEventDate(), ofPattern(Patterns.DATE_PATTERN))
                .isAfter(LocalDateTime.now())) {
            throw new NotValidException("Начало события не подходит.");
        }

        if (LocalDateTime.parse(newEventDto.getEventDate(), ofPattern(Patterns.DATE_PATTERN))
                .isBefore(LocalDateTime.now().plusHours(2))) {
            throw new WrongEventDateException("Начало события не может быть раньше чем через " +
                    "два часа с этого момента.");
        }

        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Неверные данные id пользователя"));

        Event event = EventMapper.toEvent(newEventDto);
        event.setInitiator(user);
        Location location = locationRepository.save(LocationMapper.toLocation(newEventDto.getLocation()));
        event.setLocation(location);

        EventFullDto ans = EventMapper.toEventFoolDtoForSave(eventRepository.save(event));
        ans.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        return ans;
    }

    @Override
    public List<EventShortDto> findEvents(Long id, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return eventRepository.findEventByInitiatorId(id, pageable)
                .getContent().stream().map(EventMapper::toEventShortDto).collect(toList());
    }

    @Override
    public EventFullDto findEvent(Long idUser, Long idEvent) {
        Event event = eventRepository.findEventByIdAndInitiatorId(idEvent, idUser);
        EventFullDto ans = EventMapper.toEventFoolDtoForUser(event);
        return ans;
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long idUser, Long idEvent, UpdateEventUserRequestDto updateEventUserRequestDto) {
        Event event = eventRepository.findEventByIdAndInitiatorId(idEvent, idUser);
        if (event.getPublishedOn() != null) {
            throw new DataIntegrityViolationException("Событие уже опубликованно");
        }

        if (updateEventUserRequestDto.getEventDate() != null && !LocalDateTime
                .parse(updateEventUserRequestDto.getEventDate(), ofPattern(Patterns.DATE_PATTERN))
                .isAfter(LocalDateTime.now())) {
            throw new NotValidException("Начало события не подходит.");
        }

        if (updateEventUserRequestDto.getLocation() != null) {
            updateEventUserRequestDto.setLocation(LocationMapper
                    .toLocationDto(locationRepository.save(LocationMapper
                            .toLocation(updateEventUserRequestDto.getLocation()))));
        }

        if (updateEventUserRequestDto.getStateAction() != null) {
            if (updateEventUserRequestDto.getStateAction().equals(StateActionForUser.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            } else if (updateEventUserRequestDto.getStateAction().equals(StateActionForUser.CANCEL_REVIEW)) {
                event.setState(EventState.CANCELED);
            }
        }
        Event updatedEvent = EventMapper.toUpdatedEvent(updateEventUserRequestDto, event);
        if (updatedEvent.getLocation() == null) {
            updatedEvent.setLocation(locationRepository.save(LocationMapper.toLocation(updateEventUserRequestDto.getLocation())));
        }
        return EventMapper.toEventFoolDtoForUser(eventRepository.save(updatedEvent));
    }

    @Override
    public List<EventFullDto> findEventsForAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                                 String rangeStart, String rangeEnd, int from, int size) {

        List<Event> events = eventRepository.findEventsForAdmin(users, states, categories,
                rangeStart, rangeEnd, from, size);

        if (events.size() == 0) {
            return new ArrayList<>();
        }

        return events.stream().map(EventMapper::toEventFoolDtoForUser).collect(toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEventForAdmin(Long eventId, UpdateEventAdminRequestDto updateEventAdminRequestDto) {

        if (updateEventAdminRequestDto.getEventDate() != null && LocalDateTime.parse(updateEventAdminRequestDto.getEventDate(), ofPattern(Patterns.DATE_PATTERN))
                .isBefore(LocalDateTime.now().plusHours(2))) {
            throw new NotValidException("Дата события должна быть минимум на два часа позже текущего события.");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Такого события не существует"));

        if (event.getState().equals(EventState.PUBLISHED) &&
                updateEventAdminRequestDto.getStateAction().equals(StateActionForAdmin.PUBLISH_EVENT)) {
            throw new DataIntegrityViolationException("Событие уже опубликовано");
        } else if (event.getState().equals(EventState.CANCELED) &&
                updateEventAdminRequestDto.getStateAction().equals(StateActionForAdmin.PUBLISH_EVENT)) {
            throw new DataIntegrityViolationException("Нельзя опубликовать отмененное событие");
        } else if (event.getState().equals(EventState.PUBLISHED) &&
                updateEventAdminRequestDto.getStateAction().equals(StateActionForAdmin.REJECT_EVENT)) {
            throw new DataIntegrityViolationException("Нельзя отменить опубликованное событие. Show must go on!");
        }

        if (updateEventAdminRequestDto.getStateAction() == null) {
            event.setState(event.getState());
        } else if (updateEventAdminRequestDto.getStateAction().equals(StateActionForAdmin.PUBLISH_EVENT)) {
            event.setState(EventState.PUBLISHED);
        } else if (updateEventAdminRequestDto.getStateAction().equals(StateActionForAdmin.REJECT_EVENT)) {
            event.setState(EventState.CANCELED);
        }

        Event upEvent = EventMapper.toUpdatedEventForAdmin(updateEventAdminRequestDto, event);

        if (upEvent.getState().equals(EventState.PUBLISHED)) {
            upEvent.setPublishedOn(LocalDateTime.now());
        }
        EventFullDto ans = EventMapper.toEventFoolDtoForUser(eventRepository.save(upEvent));
        return ans;
    }

    @Override
    public List<EventShortDto> findEventsForPublic(String text, List<Long> categories, Boolean paid,
                                                   String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                   EventsSort sort, int from, int size, String remoteAddress) {

        if (rangeStart != null && rangeEnd != null) {
            if (LocalDateTime.parse(rangeStart, ofPattern(Patterns.DATE_PATTERN))
                    .isAfter(LocalDateTime.parse(rangeEnd, ofPattern(Patterns.DATE_PATTERN)))) {
                throw new NotValidException("Временной интервал события задан не верно");
            }
        }

        List<Event> events = eventRepository.findEventsForPublic(text, categories, paid,
                rangeStart, rangeEnd, from, size);

        if (onlyAvailable) {
            events = events.stream()
                    .filter((event -> event.getConfirmedRequests() < (long) event.getParticipantLimit()))
                    .collect(toList());
        }

        if (sort != null) {
            if (sort.equals(EventsSort.EVENT_DATE)) {
                events = events.stream()
                        .sorted(comparing(Event::getEventDate))
                        .collect(toList());
            } else {
                events = events.stream()
                        .sorted(comparing(Event::getViews))
                        .collect(toList());
            }
        }
        if (events.size() == 0) {
            return new ArrayList<>();
        }
        addHitsForEvents(remoteAddress);
        events = addViews(events);
        return events.stream().map(EventMapper::toEventShortDto).collect(toList());
    }

    @Override
    public EventFullDto findEventForPublic(Long id, String remoteAddress) {
        LocalDateTime time = LocalDateTime.now();
        Event event = eventRepository.findById(id).orElseThrow(() -> new NotFoundException(""));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Данное событие не найдено.");
        }
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app("main-service")
                .uri("/events" + "/" + id)
                .ip(remoteAddress)
                .timestamp(time.format(ofPattern(Patterns.DATE_PATTERN))).build();

        statsClient.addHit(endpointHitDto);

        event.setViews(findViews(event.getId()));
        return EventMapper.toEventFoolDtoForUser(event);
    }

    private long findViews(long eventId) {
        String[] uri = {"/events" + "/" + eventId};
        String startDate = LocalDateTime.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endDate = LocalDateTime.now().plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<String> uris = new ArrayList<>();
        Collections.addAll(uris, uri);
        ResponseEntity<Object> stats = statsClient.findStats(startDate, endDate, uri, true);
        long views = 0;
        if (stats.hasBody()) {
            List<HashMap<String, Object>> body = (List<HashMap<String, Object>>) stats.getBody();
            HashMap<Long, Long> map = new HashMap<>();
            for (int index = 0; index < body.size(); index++) {
                String u = (String) body.get(index).get("uri");
                String s = u.replace("/events/", "");
                Long f = Long.valueOf(s);
                map.put(f, Long.valueOf(String.valueOf(body.get(index).get("hits"))));
            }
            views = map.get(eventId);
        }
        return views;
    }

    private void addHitsForEvents(String remoteAddress) {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app("main-service")
                .uri("/events")
                .ip(remoteAddress)
                .timestamp(LocalDateTime.now().format(ofPattern(Patterns.DATE_PATTERN))).build();
        statsClient.addHit(endpointHitDto);
    }

    private List<Event> addViews(List<Event> events) {
        String startDate = LocalDateTime.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endDate = LocalDateTime.now().plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String[] uri = new String[events.size()];
        for (int index = 0; index < events.size(); index++) {
            uri[index] = "/events" + "/" + events.get(index).getId();
        }
        ResponseEntity<Object> stats = statsClient.findStats(startDate, endDate, uri, true);
        if (stats.hasBody()) {
            List<HashMap<String, Object>> body = (List<HashMap<String, Object>>) stats.getBody();
            HashMap<Long, Long> map = new HashMap<>();
            for (int index = 0; index < body.size(); index++) {
                String u = (String) body.get(index).get("uri");
                String s = u.replace("/events/", "");
                Long f = Long.valueOf(s);
                map.put(f, Long.valueOf(String.valueOf(body.get(index).get("hits"))));
            }
            for (Event event : events) {
                event.setViews(map.get(event.getId()));
            }
        }
        return events;
    }
}