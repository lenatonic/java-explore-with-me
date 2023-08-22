package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.event.repository.EventRepository;
import ru.practicum.location.Location;
import ru.practicum.location.LocationRepository;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.util.Patterns;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
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
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public EventFoolDto addEvent(NewEventDto newEventDto, Long id) {
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
        ans.addAll(events.stream().map(EventMapper::toEventShortDto).collect(toList()));
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

        if (updateEventUserRequest.getEventDate() != null && !LocalDateTime
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
            } else if (updateEventUserRequest.getStateAction().equals(StateActionForUser.CANCEL_REVIEW)) {
                event.setState(EventState.CANCELED);
            }
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
        var builder = entityManager.getCriteriaBuilder();
        var query = builder.createQuery(Event.class);
        var root = query.from(Event.class);
        var criteria = builder.conjunction();

        var start = rangeStart == null ? null : LocalDateTime.parse(rangeStart, ofPattern(Patterns.DATE_PATTERN));
        var end = rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, ofPattern(Patterns.DATE_PATTERN));

        if (rangeStart != null)
            criteria = builder.and(criteria, builder.greaterThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), start));

        if (rangeEnd != null)
            criteria = builder.and(criteria, builder.lessThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), end));

        if (categories != null && categories.size() > 0)
            criteria = builder.and(criteria, root.get("category").in(categories));

        if (users != null && users.size() > 0)
            criteria = builder.and(criteria, root.get("initiator").in(users));

        if (states != null)
            criteria = builder.and(criteria, root.get("state").in(states));

        query.select(root).where(criteria);

        var events = entityManager.createQuery(query)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();

        if (events.size() == 0) return new ArrayList<>();

        return events.stream().map(EventMapper::toEventFoolDtoForUser).collect(toList());
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

        if (event.getState().equals(EventState.PUBLISHED) &&
                updateEventAdminRequest.getStateAction().equals(StateActionForAdmin.PUBLISH_EVENT)) {
            throw new DataIntegrityViolationException("Событие уже опубликовано");
        } else if (event.getState().equals(EventState.CANCELED) &&
                updateEventAdminRequest.getStateAction().equals(StateActionForAdmin.PUBLISH_EVENT)) {
            throw new DataIntegrityViolationException("Нельзя опубликовать отмененное событие");
        } else if (event.getState().equals(EventState.PUBLISHED) &&
                updateEventAdminRequest.getStateAction().equals(StateActionForAdmin.REJECT_EVENT)) {
            throw new DataIntegrityViolationException("Нельзя отменить опубликованное событие. Show must go on!");
        }

        if (updateEventAdminRequest.getStateAction() == null) {
            event.setState(event.getState());
        } else if (updateEventAdminRequest.getStateAction().equals(StateActionForAdmin.PUBLISH_EVENT)) {
            event.setState(EventState.PUBLISHED);
        } else if (updateEventAdminRequest.getStateAction().equals(StateActionForAdmin.REJECT_EVENT)) {
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
                                                   EventsSort sort, int from, int size, HttpServletRequest request) {

        if (rangeStart != null && rangeEnd != null) {
            if (LocalDateTime.parse(rangeStart, ofPattern(Patterns.DATE_PATTERN))
                    .isAfter(LocalDateTime.parse(rangeEnd, ofPattern(Patterns.DATE_PATTERN)))) {
                throw new NotValidException("Временной интервал события задан не верно");
            }
        }

        var start = rangeStart == null ? null : LocalDateTime.parse(rangeStart, ofPattern(Patterns.DATE_PATTERN));
        var end = rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, ofPattern(Patterns.DATE_PATTERN));

        var builder = entityManager.getCriteriaBuilder();
        var query = builder.createQuery(Event.class);
        var root = query.from(Event.class);
        var criteria = builder.conjunction();

        if (text != null) {
            criteria = builder.and(criteria, builder.or(
                    builder.like(
                            builder.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                    builder.like(
                            builder.lower(root.get("description")), "%" + text.toLowerCase() + "%")));
        }

        if (categories != null && categories.size() > 0)
            criteria = builder.and(criteria, root.get("category").in(categories));

        if (paid != null) {
            Predicate predicate;
            if (paid) predicate = builder.isTrue(root.get("paid"));
            else predicate = builder.isFalse(root.get("paid"));
            criteria = builder.and(criteria, predicate);
        }

        if (rangeEnd != null)
            criteria = builder.and(criteria, builder.lessThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), end));

        if (rangeStart != null)
            criteria = builder.and(criteria, builder.greaterThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), start));

        query.select(root).where(criteria).orderBy(builder.asc(root.get("eventDate")));

        List<Event> events = entityManager.createQuery(query)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();

        if (onlyAvailable)
            events = events.stream()
                    .filter((event -> event.getConfirmedRequests() < (long) event.getParticipantLimit()))
                    .collect(toList());

        if (sort != null) {
            if (sort.equals(EventsSort.EVENT_DATE))
                events = events.stream()
                        .sorted(comparing(Event::getEventDate))
                        .collect(toList());
            else
                events = events.stream()
                        .sorted(comparing(Event::getViews))
                        .collect(toList());
        }
        if (events.size() == 0) {
            return new ArrayList<>();
        }
        addHitsForEvents(events, request);
        addViews(events);
        return events.stream().map(EventMapper::toEventShortDto).collect(toList());
    }

    @Transactional
    @Override
    public EventFoolDto findEventForPublic(Long id, HttpServletRequest request) {
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

        event.setViews((long) findViews(event.getId()));
        eventRepository.save(event);
        return EventMapper.toEventFoolDtoForUser(event);
    }

    private int findViews(long eventId) {
        String[] uri = {"/events/" + eventId};
        String startDate = LocalDateTime.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endDate = LocalDateTime.now().plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<String> uris = new ArrayList<>();
        Collections.addAll(uris, uri);
        ResponseEntity<Object> stats = statsClient.findStats(startDate, endDate, uri, true);
        int views = 0;
        if (stats.hasBody()) {
            List<HashMap<String, Object>> body = (List<HashMap<String, Object>>) stats.getBody();
            if (body != null && !body.isEmpty()) {
                HashMap<String, Object> map = body.get(0);
                views = (int) map.get("hits");
            }
        }
        System.out.println(views);
        return views;
    }

    private void addHitsForEvents(List<Event> events, HttpServletRequest request) {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app("main-service")
                .uri("/events/")
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(ofPattern(Patterns.DATE_PATTERN))).build();
        statsClient.addHit(endpointHitDto);
        addHitsForListEvents(events, request, LocalDateTime.now());
    }

    private void addHitsForListEvents(List<Event> events, HttpServletRequest request, LocalDateTime time) {
        for (Event event : events) {
            EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                    .app("main-service")
                    .uri("/events/" + event.getId())
                    .ip(request.getRemoteAddr())
                    .timestamp(time.format(ofPattern(Patterns.DATE_PATTERN))).build();
            statsClient.addHit(endpointHitDto);
        }
    }

    private void addViews(List<Event> events) {
        for (Event event : events) {
            Long views = Long.valueOf(findViews(event.getId()));
            event.setViews(views);
            eventRepository.save(event);
        }
    }
}