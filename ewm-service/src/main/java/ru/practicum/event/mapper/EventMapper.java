package ru.practicum.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
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
                .description(newEventDto.getDescription())
                .category(Category.builder().id(newEventDto.getCategory()).build())
                .location(newEventDto.getLocation())
                .paid(newEventDto.isPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .state(EventState.PENDING)
                .requestModeration(newEventDto.getRequestModeration())
                .title(newEventDto.getTitle())
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
                .publishedOn(event.getPublishedOn() == null ? null : event.getPublishedOn()
                        .format(DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)))
                .requestModeration(event.getRequestModeration())
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

    public EventFoolDto toEventFoolDtoForUser(Event event) {
        return EventFoolDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .eventDate(event.getEventDate().format(DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)))
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

    public Event toEvent(UpdateEventUserRequest updateEventUserRequest) {
        return Event.builder()
                .annotation(updateEventUserRequest.getAnnotation())
                .eventDate(LocalDateTime.parse(updateEventUserRequest.getEventDate(),
                        DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)))
                .description(updateEventUserRequest.getDescription())
                .category(Category.builder().id(updateEventUserRequest.getCategory()).build())
                .location(updateEventUserRequest.getLocation())
                .paid(updateEventUserRequest.isPaid())
                .participantLimit(updateEventUserRequest.getParticipantLimit())
                .requestModeration(updateEventUserRequest.getRequestModeration())
                .state(updateEventUserRequest.getStateAction()
                        .equals("CANCEL_REVIEW") ? EventState.CANCELED : EventState.PENDING)
                .title(updateEventUserRequest.getTitle())
                .build();
    }

    public Event toUpdatedEvent(UpdateEventUserRequest updateEventUserRequest, Event event) {
        return Event.builder()
                .id(event.getId())
                .annotation(updateEventUserRequest.getAnnotation() != null ? updateEventUserRequest.getAnnotation() : event.getAnnotation())
                .category(updateEventUserRequest.getCategory() != null ? Category.builder()
                        .id(updateEventUserRequest.getCategory()).build() : event.getCategory())
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(updateEventUserRequest.getDescription() != null ? updateEventUserRequest.getDescription() : event.getDescription())
                .eventDate(updateEventUserRequest.getEventDate() != null ? LocalDateTime.parse(updateEventUserRequest.getEventDate(),
                        DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)) : event.getEventDate())
                .initiator(event.getInitiator())
                .location(updateEventUserRequest.getLocation() != null ? updateEventUserRequest.getLocation() : event.getLocation())
                .paid(event.isPaid())
                .participantLimit(updateEventUserRequest.getParticipantLimit() != 0 ? updateEventUserRequest.getParticipantLimit() : event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .state(updateEventUserRequest.getStateAction()
                        .equals("CANCEL_REVIEW") ? EventState.CANCELED : EventState.PENDING)
                .requestModeration(updateEventUserRequest.getRequestModeration() == null ? event.getRequestModeration() : updateEventUserRequest.getRequestModeration())
                .title(updateEventUserRequest.getTitle() != null ? updateEventUserRequest.getTitle() : event.getTitle())
                .views(event.getViews())
                .build();

    }

    public Event toUpdatedEventForAdmin(UpdateEventAdminRequest updateEventAdminRequest, Event event) {
        return Event.builder()
                .id(event.getId())
                .annotation(updateEventAdminRequest.getAnnotation() != null ? updateEventAdminRequest.getAnnotation() : event.getAnnotation())
                .category(updateEventAdminRequest.getCategory() != null ? Category.builder()
                        .id(updateEventAdminRequest.getCategory()).build() : event.getCategory())
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(updateEventAdminRequest.getDescription() != null ? updateEventAdminRequest.getDescription() : event.getDescription())
                .eventDate(updateEventAdminRequest.getEventDate() != null ? LocalDateTime.parse(updateEventAdminRequest.getEventDate(),
                        DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)) : event.getEventDate())
                .initiator(event.getInitiator())
                .location(updateEventAdminRequest.getLocation() == null ? event.getLocation() : updateEventAdminRequest.getLocation())
                .paid(updateEventAdminRequest.getPaid() == null ? event.isPaid() : updateEventAdminRequest.getPaid())
                .participantLimit(updateEventAdminRequest.getParticipantLimit() != 0 ? updateEventAdminRequest.getParticipantLimit() : event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .state(updateEventAdminRequest.getStateAction()
                        .equals("PUBLISH_EVENT") ? EventState.CANCELED : EventState.PUBLISHED)
                .requestModeration(updateEventAdminRequest.getRequestModeration() == null ? event.getRequestModeration() : updateEventAdminRequest.getRequestModeration())
                .title(updateEventAdminRequest.getTitle() != null ? updateEventAdminRequest.getTitle() : event.getTitle())
                .views(event.getViews())
                .build();
    }
}