package ru.practicum.event.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.util.Patterns;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, LocationRepository locationRepository,
                            UserRepository userRepository, CategoryRepository categoryRepository) {
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public EventFoolDto addEvent(NewEventDto newEventDto, Long id) {
        if (LocalDateTime.parse(newEventDto.getEventDate(), DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN))
                .isBefore(LocalDateTime.now().plusHours(2))) {
            throw new WrongEventDateException("The date of Event is scheduled cannot be later");
        }
        if (LocalDateTime.parse(newEventDto.getEventDate(), DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN))
                .isBefore(LocalDateTime.now().plusHours(2))) {
            throw new WrongEventDateException("The date of Event is scheduled cannot be earlier" +
                    "than two hours from the current moment");
        }
        if(newEventDto.getRequestModeration() == null) {
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

    @Transactional
    @Override
    public EventFoolDto updateEvent(Long idUser, Long idEvent, UpdateEventUserRequest updateEventUserRequest) {
        Event event = eventRepository.findEventByIdAndInitiator_Id(idEvent, idUser);
        if (updateEventUserRequest.getLocation() != null) {
            updateEventUserRequest.setLocation(locationRepository.save(updateEventUserRequest.getLocation()));
        }
        Event updatedEvent = EventMapper.toUpdatedEvent(updateEventUserRequest, event);
        if (updatedEvent.getLocation() == null) {
            updatedEvent.setLocation(locationRepository.save(updateEventUserRequest.getLocation()));
        }
        return EventMapper.toEventFoolDtoForUser(eventRepository.save(updatedEvent));
    }

    @Transactional
    @Override
    public List<EventFoolDto> findEventsForAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                                 String rangeStart, String rangeEnd, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);

        LocalDateTime start = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN));
        LocalDateTime end = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN));

        return eventRepository.findByInitiator_IdInAndStateInAndCategory_IdInAndEventDateIsAfterAndEventDateIsBefore(users,
                        states, categories, start, end, pageable).getContent()
                .stream().map(EventMapper::toEventFoolDtoForUser).collect(Collectors.toList());
    }

    @Transactional
    @Override
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
}