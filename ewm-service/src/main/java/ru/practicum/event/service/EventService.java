package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.event.model.EventState;

import java.util.List;

public interface EventService {
    EventFullDto addEvent(NewEventDto newEventDto, Long id);

    List<EventShortDto> findEvents(Long id, int from, int size);

    EventFullDto findEvent(Long idUser, Long idEvent);

    EventFullDto updateEvent(Long idUser, Long idEvent, UpdateEventUserRequestDto updateEventUserRequestDto);

    List<EventFullDto> findEventsForAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                          String rangeStart, String rangeEnd, int from, int size);

    EventFullDto updateEventForAdmin(Long eventId, UpdateEventAdminRequestDto updateEventAdminRequestDto);

    List<EventShortDto> findEventsForPublic(String text, List<Long> categories, Boolean paid,
                                            String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                            EventsSort sort, int from, int size, String remoteAddress);

    EventFullDto findEventForPublic(Long id, String remoteAddress);
}