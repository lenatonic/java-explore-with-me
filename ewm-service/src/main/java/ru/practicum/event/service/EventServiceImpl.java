package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.error.exceptions.NotFoundException;
import ru.practicum.error.exceptions.NotValidException;
import ru.practicum.error.exceptions.WrongEventDateException;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.Location;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.location.LocationRepository;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.util.Patterns;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ofPattern;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    @Override
    @Transactional
    public EventFoolDto addEvent(NewEventDto newEventDto, Long id) {
        if(newEventDto.getEventDate() != null && !LocalDateTime
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
        Event event = EventMapper.toEvent(newEventDto);
        event.setInitiator(User.builder().id(id).build());
        Location location = locationRepository.save(newEventDto.getLocation());
        event.setLocation(location);

        EventFoolDto ans = EventMapper.toEventFoolDtoForSave(eventRepository.save(event));

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Неверные данные id пользователя"));
        ans.setInitiator(UserMapper.toUserShortDto(user));

        Category category = categoryRepository.findById(event.getCategory().getId())
                .orElseThrow(() -> new NotFoundException("Такой категории пока не существует."));
        ans.setCategory(CategoryMapper.toCategoryDto(category));
        return ans;
    }

    @Override
    @Transactional
    public List<EventShortDto> findEvents(Long id, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<EventShortDto> ans = new ArrayList<>();
        List<Event> events = eventRepository.findEventByInitiator_Id(id, pageable).getContent();
        ans.addAll(events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList()));
        return ans;
    }

    @Override
    @Transactional
    public EventFoolDto findEvent(Long idUser, Long idEvent) {
        Event event = eventRepository.findEventByIdAndInitiator_Id(idEvent, idUser);
        EventFoolDto ans = EventMapper.toEventFoolDtoForUser(event);
        return ans;
    }

    @Override
    @Transactional
    public EventFoolDto updateEvent(Long idUser, Long idEvent, UpdateEventUserRequest updateEventUserRequest) {
        Event event = eventRepository.findEventByIdAndInitiator_Id(idEvent, idUser);
        if (event.getPublishedOn() != null) {
            throw new DataIntegrityViolationException("Событие уже опубликованно");
        }

        if(updateEventUserRequest.getEventDate() != null && !LocalDateTime
                .parse(updateEventUserRequest.getEventDate(), ofPattern(Patterns.DATE_PATTERN))
                .isAfter(LocalDateTime.now())) {
            throw new NotValidException("Начало события не подходит.");
        }

        if (updateEventUserRequest.getLocation() != null) {
            updateEventUserRequest.setLocation(locationRepository.save(updateEventUserRequest.getLocation()));
        }

        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction().equals(StateActionForUser.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            } else
                event.setState(EventState.CANCELED);
        }
        Event updatedEvent = EventMapper.toUpdatedEvent(updateEventUserRequest, event);
        if (updatedEvent.getLocation() == null) {
            updatedEvent.setLocation(locationRepository.save(updateEventUserRequest.getLocation()));
        }
        return EventMapper.toEventFoolDtoForUser(eventRepository.save(updatedEvent));
    }

    @Override
    @Transactional
    public List<EventFoolDto> findEventsForAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                                 String rangeStart, String rangeEnd, int from, int size) {
        Pageable pageable = PageRequest.of(from/size, size);

        LocalDateTime start = rangeStart == null ? LocalDateTime.now() :LocalDateTime
                .parse(rangeStart, ofPattern(Patterns.DATE_PATTERN));
        LocalDateTime end = rangeEnd == null ? LocalDateTime.now() : LocalDateTime
                .parse(rangeEnd, ofPattern(Patterns.DATE_PATTERN));

        return findResponse(users, states, categories, start, end, pageable).stream()
                .map(EventMapper::toEventFoolDtoForUser).collect(Collectors.toList());

//        return eventRepository.findByInitiator_IdInAndStateInAndCategory_IdInAndEventDateIsAfterAndEventDateIsBefore(users,
//                        states, categories, start, end, pageable).getContent()
//                .stream().map(EventMapper::toEventFoolDtoForUser).collect(Collectors.toList());
    }

    private List<Event> findResponse(List<Long> users, List<EventState> states, List<Long> categories,
                                     LocalDateTime start, LocalDateTime end, Pageable pageable) {
        if(users == null && states != null && categories != null) {
            return eventRepository.findAllByStateInAndCategory_IdInAndEventDateBetween(states, categories,
                    start, end, pageable);
        }
        if(users == null && states == null && categories != null) {
            return eventRepository.findAllByCategory_IdInAndEventDateBetween(categories,
                    start, end, pageable);
        }
        if(users == null && states == null && categories == null) {
            return eventRepository.findAllByEventDateBetween(start, end, pageable);
        }
        if(states == null && users != null && categories != null) {
            return eventRepository.findAllByInitiator_IdInAndCategory_IdInAndEventDateBetween(users, categories,
                    start, end, pageable);
        }
        if(states == null && users != null && categories == null) {
            return eventRepository.findAllByInitiator_IdInAndEventDateBetween(users, start, end, pageable);
        }
        if(states != null && users != null && categories == null) {
            return eventRepository.findAllByInitiator_IdInAndStateInAndEventDateBetween(users, states, start, end,
                    pageable);
        }
        else {
            return eventRepository.findByInitiator_IdInAndStateInAndCategory_IdInAndEventDateIsAfterAndEventDateIsBefore(users,
                            states, categories, start, end, pageable).getContent();
        }
    }

    @Override
    @Transactional
    public EventFoolDto updateEventForAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {

        if (updateEventAdminRequest.getEventDate() != null && LocalDateTime.parse(updateEventAdminRequest.getEventDate(), ofPattern(Patterns.DATE_PATTERN))
                .isBefore(LocalDateTime.now().plusHours(2))) {
            throw new NotValidException("Дата события должна быть минимум на два часа позже текущего события.");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Такого события не существует"));
        if(updateEventAdminRequest.getStateAction() == null) {
            event.setState(event.getState());
        } else if(updateEventAdminRequest.getStateAction().equals(StateActionForAdmin.PUBLISH_EVENT)) {
            event.setState(EventState.PUBLISHED);
        } else if(updateEventAdminRequest.getStateAction().equals(StateActionForAdmin.REJECT_EVENT)) {
            event.setState(EventState.CANCELED);
        }
        Event upEvent = EventMapper.toUpdatedEventForAdmin(updateEventAdminRequest, event);

        if (upEvent.getState().equals(EventState.PUBLISHED)) {
            upEvent.setPublishedOn(LocalDateTime.now());
        }
        EventFoolDto ans = EventMapper.toEventFoolDtoForUser(eventRepository.save(upEvent));
        return ans;
    }

    @Override
    @Transactional
    public List<EventShortDto> findEventsForPublic(String text, List<Long> categories, Boolean paid,
                                                   String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                   EventsSort sort, Integer from, Integer size, HttpServletRequest request) {


        LocalDateTime start = rangeStart == null ? LocalDateTime.now() : LocalDateTime.
                parse(rangeStart, ofPattern(Patterns.DATE_PATTERN));
        LocalDateTime end = rangeEnd == null ? LocalDateTime.now().plusYears(1) : LocalDateTime
                .parse(rangeEnd, ofPattern(Patterns.DATE_PATTERN));

        if (start.isBefore(LocalDateTime.now()) || end.isBefore(LocalDateTime.now())) {
            throw new NotValidException("Временные рамки заданы не корректно");
        }

        String filter = null;
        if (sort == null) {
            filter = "id";
        } else if (sort.equals(EventsSort.EVENT_DATE.toString())) {
            filter = "eventDate";
        } else if (sort.equals(EventsSort.VIEWS.toString())) {
            filter = "views";
        }
//         else {
//            filter = "id";
//        }
//        if (start == null) {
//            start = LocalDateTime.now();
//        }
//        if (end == null) {
//            end = LocalDateTime.now().plusYears(1);
//        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(filter));

        List<Event> events = eventRepository.getFilterEvents(text, categories, paid, start, end, pageable);
        if (onlyAvailable) {
            events.removeIf(event -> requestRepository.findAllByEvent_IdAndStatus(event.getId(), RequestStatus.CONFIRMED).size() == event.getParticipantLimit());
        }
        //eventRepository.saveAll(events);
        addHits(events, request);
        //findAndSaveViews(events);
        return events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFoolDto findEventForPublic(@PathVariable Long id, HttpServletRequest request) {
        LocalDateTime time = LocalDateTime.now();
        Event event = eventRepository.findById(id).orElseThrow(() -> new NotFoundException(""));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Данное событие не найдено.");
        }
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app("main-service")
                .uri("/events/" + id)
                .ip(request.getRemoteAddr())
                .timestamp(time.format(ofPattern(Patterns.DATE_PATTERN))).build();
        statsClient.addHit(endpointHitDto);
        //addView(event);
        return EventMapper.toEventFoolDtoForUser(event);
    }

    private void addHits(List<Event> events, HttpServletRequest request) {
        LocalDateTime time = LocalDateTime.now();
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .ip(request.getRemoteAddr())
                .uri("/events/")
                .app("main-service")
                .timestamp(time.format(ofPattern(Patterns.DATE_PATTERN))).build();
        statsClient.addHit(endpointHitDto);
        addHitsForEventsList(events, endpointHitDto);
    }

    private void addHitsForEventsList(List<Event> events, EndpointHitDto endpointHitDto) {
        for (Event event : events) {
            EndpointHitDto endpoint = EndpointHitDto.builder()
                    .ip(endpointHitDto.getIp())
                    .uri(endpointHitDto.getUri() + event.getId())
                    .app(endpointHitDto.getApp())
                    .timestamp(endpointHitDto.getTimestamp()).build();
            statsClient.addHit(endpoint);
        }
    }

    private void addView(Event event) {
        String start = event.getCreatedOn().format(ofPattern(Patterns.DATE_PATTERN));
        String end = LocalDateTime.now().format(ofPattern(Patterns.DATE_PATTERN));
        List<String> uris = List.of("/events/" + event.getId());
        ResponseEntity<Object> stats = statsClient.findStats(start, end, uris, false);
        List<ViewStatsDto> s = (List<ViewStatsDto>) stats.getBody();
//        List<ViewStatsDto> stats = (List<ViewStatsDto>) statsClient.findStats(start, end, uris, false);
        event.setViews(s.get(0).getHits());
//        }

//    private void findAndSaveViews(List<Event> events) {
//        for (Event event : events) {
//            event.setViews(event.getViews() + 1);
//        }
//        eventRepository.saveAll(events);
//    }
//
//    private void findAndSaveView(Event event) {
//        event.setViews(event.getViews() + 1);
//        eventRepository.save(event);
    }
}