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
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.EventFoolDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
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
@Transactional(readOnly = true)
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
    public List<EventShortDto> findEvents(Long id, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<EventShortDto> ans = new ArrayList<>();
        List<Event> events = eventRepository.findEventByInitiator_Id(id, pageable).getContent();
        ans.addAll(events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList()));
        return ans;
    }

    @Override
    public EventFoolDto findEvent(Long idUser, Long idEvent) {
        Event event = eventRepository.findEventByIdAndInitiator_Id(idEvent, idUser);
        EventFoolDto ans = EventMapper.toEventFoolDtoForUser(event);
        return ans;
    }

    @Transactional
    @Override
    public EventFoolDto updateEvent(Long idUser, Long idEvent, UpdateEventUserRequest updateEventUserRequest) {
        Event event = eventRepository.findEventByIdAndInitiator_Id(idEvent, idUser);
        Event updatedEvent = EventMapper.toUpdatedEvent(updateEventUserRequest, event);
        return EventMapper.toEventFoolDtoForUser(eventRepository.save(updatedEvent));
    }

    @Override
    public List<EventFoolDto> findEventsForAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                          String rangeStart, String rangeEnd, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        LocalDateTime start = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN));
        LocalDateTime end = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN));
//        if (categories.size() == 1 && categories.contains(0)) {
//        categories = null;
//        }
//
//        if (users.size() == 1 && users.contains(0L)) {
//            users = null;
//        }
//        if (states.size() == 1 && states.contains(0L)) {
//            states = null;
//        }

//        if(users == null && categories == null) {
//            return eventRepository.findByStateInAndEventDateIsBetween(states, start, end, pageable).stream().map(EventMapper::toEventFoolDto).collect(Collectors.toList());
//        }

//        if(users == null) {
//            return eventRepository.findByStateInAndCategory_IdInAndEventDateIsBetween(states, categories, start, end, pageable).stream().map(EventMapper::toEventFoolDto).collect(Collectors.toList());
//        }
//        return eventRepository.findEventWhereInitiator_IdInAndStateInAndCategory_IdInAndEventDateIsBetween(users, states, categories, start, end, pageable).getContent();
    List<Event> events = eventRepository.searchEventsByAdmin(users, states, categories, start, end, pageable);
    return events.stream().map(EventMapper::toEventFoolDtoForUser).collect(Collectors.toList());
    }
}