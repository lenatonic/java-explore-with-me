package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.error.exceptions.NotFoundException;
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
import java.util.Collection;
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
        if (LocalDateTime.parse(newEventDto.getEventDate(), ofPattern(Patterns.DATE_PATTERN))
                .isBefore(LocalDateTime.now().plusHours(2))) {
            throw new WrongEventDateException("The date of Event is scheduled cannot be later");
        }
        if (LocalDateTime.parse(newEventDto.getEventDate(), ofPattern(Patterns.DATE_PATTERN))
                .isBefore(LocalDateTime.now().plusHours(2))) {
            throw new WrongEventDateException("The date of Event is scheduled cannot be earlier" +
                    "than two hours from the current moment");
        }
        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
        }
        System.out.println(newEventDto.getRequestModeration());
        Event event = EventMapper.toEvent(newEventDto);
        System.out.println(event.getRequestModeration());
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
        if (event.getPublishedOn() != null)
            throw new DataIntegrityViolationException("Событие уже опубликованно");

        if (updateEventUserRequest.getLocation() != null) {
            updateEventUserRequest.setLocation(locationRepository.save(updateEventUserRequest.getLocation()));
        }

        if (updateEventUserRequest.getStateAction() != null) {
            if ("SEND_TO_REVIEW".equals(updateEventUserRequest.getStateAction()))
                event.setState(EventState.PENDING);
            else
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
        Pageable pageable = PageRequest.of(from / size, size);

        LocalDateTime start = LocalDateTime.parse(rangeStart, ofPattern(Patterns.DATE_PATTERN));
        LocalDateTime end = LocalDateTime.parse(rangeEnd, ofPattern(Patterns.DATE_PATTERN));

        return eventRepository.findByInitiator_IdInAndStateInAndCategory_IdInAndEventDateIsAfterAndEventDateIsBefore(users,
                        states, categories, start, end, pageable).getContent()
                .stream().map(EventMapper::toEventFoolDtoForUser).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFoolDto updateEventForAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Такого события не существует"));

        Event upEvent = EventMapper.toUpdatedEventForAdmin(updateEventAdminRequest, event);

        if (upEvent.getState().equals(EventState.PUBLISHED)) {
            upEvent.setPublishedOn(LocalDateTime.now());
        }
        EventFoolDto ans = EventMapper.toEventFoolDtoForUser(eventRepository.save(upEvent));
        return ans;
    }

    @Override
    public List<EventShortDto> findEventsForPublic(String text, List<Long> categories, Boolean paid,
                                                   String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                   EventsSort sort, Integer from, Integer size, HttpServletRequest request) {
        LocalDateTime start = LocalDateTime.parse(rangeStart, ofPattern(Patterns.DATE_PATTERN));
        LocalDateTime end = LocalDateTime.parse(rangeEnd, ofPattern(Patterns.DATE_PATTERN));

        String filter;
        if (sort.equals(EventsSort.EVENT_DATE.toString())) {
            filter = "eventDate";
        }
        else if (sort.equals(EventsSort.VIEWS.toString())) {
            filter = "views";
        } else {
            filter = "id";
        }
        if (start == null) {
            start = LocalDateTime.now();
        }
        if (end == null) {
            end = LocalDateTime.now().plusYears(1);
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(filter));

        List<Event> events = eventRepository.getFilterEvents(text, categories, paid, start, end, pageable);
        if (onlyAvailable) {
            events.removeIf(event -> requestRepository.findByEvent_IdAndStatus(event.getId(), RequestStatus.CONFIRMED).size() == event.getParticipantLimit());
        }
        //eventRepository.saveAll(events);
        addHits(events, request);
        //findAndSaveViews(events);
        return events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    public EventFoolDto findEventForPublic(@PathVariable Long id, HttpServletRequest request) {
        LocalDateTime time = LocalDateTime.now();
        Event event = eventRepository.findById(id).orElseThrow(() -> new NotFoundException(""));
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app("main-service")
                .uri("/events/" +id)
                .ip(request.getRemoteAddr())
                .timestamp(time.format(ofPattern(Patterns.DATE_PATTERN))).build();
        statsClient.addHit(endpointHitDto);
//        addView(event);
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

    private void addHitsForEventsList(List<Event> events,EndpointHitDto endpointHitDto) {
        for(Event event : events) {
            EndpointHitDto endpoint = EndpointHitDto.builder()
                    .ip(endpointHitDto.getIp())
                    .uri(endpointHitDto.getUri() + event.getId())
                    .app(endpointHitDto.getApp())
                    .timestamp(endpointHitDto.getTimestamp()).build();
            statsClient.addHit(endpoint);
        }
    }

//    private void addView(Event event) {
//        var start = event.getCreatedOn().format(ofPattern(Patterns.DATE_PATTERN));
//        var end = LocalDateTime.now().format(ofPattern(Patterns.DATE_PATTERN));
//        var uris = List.of("/events/" + event.getId());
//        List<ViewStatsDto> stats = (List<ViewStatsDto>) statsClient.findStats(start, end, uris, false);
//        event.setViews(stats.get(0).getHits());
//        }

    private void findAndSaveViews(List<Event> events) {
        for (Event event : events) {
            event.setViews(event.getViews() + 1);
        }
        eventRepository.saveAll(events);
    }

    private void findAndSaveView(Event event) {
        event.setViews(event.getViews() + 1);
        eventRepository.save(event);
    }
}