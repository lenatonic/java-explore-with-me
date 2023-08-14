package ru.practicum.event.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.error.exceptions.NotFoundException;
import ru.practicum.error.exceptions.WrongEventDateException;
import ru.practicum.event.dto.EventAddDto;
import ru.practicum.event.dto.EventFoolDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
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
    public EventFoolDto addEvent(EventAddDto eventAddDto, Long id) {
        if (LocalDateTime.parse(eventAddDto.getEventDate(), DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN))
                .isBefore(LocalDateTime.now().plusHours(2))) {
            throw new WrongEventDateException("The date of Event is scheduled cannot be later");
        }
        if (LocalDateTime.parse(eventAddDto.getEventDate(), DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN))
                .isBefore(LocalDateTime.now().plusHours(2))) {
            throw new WrongEventDateException("The date of Event is scheduled cannot be earlier" +
                    "than two hours from the current moment");
        }
        Event event = EventMapper.toEvent(eventAddDto);
        event.setInitiator(User.builder().id(id).build());
        Location location = locationRepository.save(eventAddDto.getLocation());
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
}