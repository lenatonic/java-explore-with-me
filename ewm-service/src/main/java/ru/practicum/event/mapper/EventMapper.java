package ru.practicum.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventAddDto;
import ru.practicum.event.dto.EventFoolDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.util.Patterns;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class EventMapper {
    public Event toEvent(EventAddDto eventAddDto) {
        return Event.builder()
                .annotation(eventAddDto.getAnnotation())
                .eventDate(LocalDateTime.parse(eventAddDto.getEventDate(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .createdOn(LocalDateTime.now())
                .description(eventAddDto.getDescription())
                .category(Category.builder().id(eventAddDto.getCategory()).build())
                .location(eventAddDto.getLocation())
                .paid(eventAddDto.isPaid())
                .participantLimit(eventAddDto.getParticipantLimit())
                .requestModeration(eventAddDto.isRequestModeration())
                .title(eventAddDto.getTitle())
                .build();
    }

    public EventFoolDto toEventFoolDto(Event event) {
        return EventFoolDto.builder()
                .annotation(event.getAnnotation())
                .eventDate(event.getEventDate().format(DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)))
                .description(event.getDescription())
                .category(CategoryDto.builder().id(event.getCategory().getId()).build())
                .initiator(UserShortDto.builder().id(event.getInitiator().getId()).build())
                .location(event.getLocation())
                .paid(event.isPaid())
                .publishedOn(event.getPublishedOn().format(
                        DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)))
                .requestModeration(event.isRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public EventFoolDto toEventFoolDtoForSave(Event event) {
        return EventFoolDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .eventDate(event.getEventDate().format(DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)))
                .description(event.getDescription())
                .category(CategoryDto.builder().id(event.getCategory().getId()).build())
                .initiator(UserShortDto.builder().id(event.getInitiator().getId()).build())
                .participantLimit(event.getParticipantLimit())
                .location(event.getLocation())
                .paid(event.isPaid())
                .requestModeration(event.isRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .eventDate(event.getEventDate().format(DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)))
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .id(event.getId())
                .paid(event.isPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }
}
