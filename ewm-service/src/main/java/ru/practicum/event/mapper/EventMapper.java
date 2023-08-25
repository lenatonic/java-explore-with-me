package ru.practicum.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.location.LocationMapper;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.util.Patterns;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class EventMapper {
    public Event toEvent(NewEventDto newEventDto) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .eventDate(LocalDateTime.parse(newEventDto.getEventDate(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .createdOn(LocalDateTime.now())
                .confirmedRequests(0)
                .description(newEventDto.getDescription())
                .category(Category.builder().id(newEventDto.getCategory()).build())
                .location(LocationMapper.toLocation(newEventDto.getLocation()))
                .paid(newEventDto.isPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .state(EventState.PENDING)
                .requestModeration(newEventDto.getRequestModeration())
                .title(newEventDto.getTitle())
                .build();
    }

    public EventFullDto toEventFoolDtoForSave(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .eventDate(event.getEventDate().format(DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn().format(DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)))
                .description(event.getDescription())
                .category(CategoryDto.builder().id(event.getCategory().getId()).build())
                .initiator(UserShortDto.builder().id(event.getInitiator().getId()).build())
                .participantLimit(event.getParticipantLimit())
                .location(event.getLocation())
                .paid(event.isPaid())
                .requestModeration(event.getRequestModeration())
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

    public EventFullDto toEventFoolDtoForUser(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .eventDate(event.getEventDate().format(DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn().format(DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)))
                .description(event.getDescription())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn() == null ? null : event.getPublishedOn()
                        .format(DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)))
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public Event toUpdatedEvent(UpdateEventUserRequestDto updateEventUserRequestDto, Event event) {
        return Event.builder()
                .id(event.getId())
                .annotation(updateEventUserRequestDto.getAnnotation() != null ? updateEventUserRequestDto.getAnnotation() : event.getAnnotation())
                .category(updateEventUserRequestDto.getCategory() != null ? Category.builder()
                        .id(updateEventUserRequestDto.getCategory()).build() : event.getCategory())
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(updateEventUserRequestDto.getDescription() != null ? updateEventUserRequestDto.getDescription() : event.getDescription())
                .eventDate(updateEventUserRequestDto.getEventDate() != null ? LocalDateTime.parse(updateEventUserRequestDto.getEventDate(),
                        DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)) : event.getEventDate())
                .initiator(event.getInitiator())
                .location(updateEventUserRequestDto.getLocation() != null ? updateEventUserRequestDto.getLocation() : event.getLocation())
                .paid(updateEventUserRequestDto.getPaid() != null ? updateEventUserRequestDto.getPaid() : event.isPaid())
                .participantLimit(updateEventUserRequestDto.getParticipantLimit() != 0 ? updateEventUserRequestDto.getParticipantLimit() : event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .state(event.getState())
                .requestModeration(updateEventUserRequestDto.getRequestModeration() == null ? event.getRequestModeration() : updateEventUserRequestDto.getRequestModeration())
                .title(updateEventUserRequestDto.getTitle() != null ? updateEventUserRequestDto.getTitle() : event.getTitle())
                .views(event.getViews())
                .build();

    }

    public Event toUpdatedEventForAdmin(UpdateEventAdminRequestDto updateEventAdminRequestDto, Event event) {
        return Event.builder()
                .id(event.getId())
                .annotation(updateEventAdminRequestDto.getAnnotation() != null ? updateEventAdminRequestDto.getAnnotation() : event.getAnnotation())
                .category(updateEventAdminRequestDto.getCategory() != null ? Category.builder()
                        .id(updateEventAdminRequestDto.getCategory()).build() : event.getCategory())
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(updateEventAdminRequestDto.getDescription() != null ? updateEventAdminRequestDto.getDescription() : event.getDescription())
                .eventDate(updateEventAdminRequestDto.getEventDate() != null ? LocalDateTime.parse(updateEventAdminRequestDto.getEventDate(),
                        DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)) : event.getEventDate())
                .initiator(event.getInitiator())
                .location(updateEventAdminRequestDto.getLocation() == null ? event.getLocation() : updateEventAdminRequestDto.getLocation())
                .paid(updateEventAdminRequestDto.getPaid() == null ? event.isPaid() : updateEventAdminRequestDto.getPaid())
                .participantLimit(updateEventAdminRequestDto.getParticipantLimit() != 0 ? updateEventAdminRequestDto.getParticipantLimit() : event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .state(event.getState())
                .requestModeration(updateEventAdminRequestDto.getRequestModeration() == null ? event.getRequestModeration() : updateEventAdminRequestDto.getRequestModeration())
                .title(updateEventAdminRequestDto.getTitle() != null ? updateEventAdminRequestDto.getTitle() : event.getTitle())
                .views(event.getViews())
                .build();
    }
}