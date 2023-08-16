package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.event.model.EventState;

import java.util.List;

public interface EventService {
    EventFoolDto addEvent(NewEventDto newEventDto, Long id);

    List<EventShortDto> findEvents(Long id, int from, int size);

    EventFoolDto findEvent(Long idUser, Long idEvent);

    EventFoolDto updateEvent(Long idUser, Long idEvent, UpdateEventUserRequest updateEventUserRequest);

    List<EventFoolDto> findEventsForAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                          String rangeStart, String rangeEnd, int from, int size);

    EventFoolDto updateEventForAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}