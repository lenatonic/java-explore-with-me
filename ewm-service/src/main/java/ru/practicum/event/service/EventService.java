package ru.practicum.event.service;

import ru.practicum.event.dto.EventAddDto;
import ru.practicum.event.dto.EventFoolDto;
import ru.practicum.event.dto.EventShortDto;

import java.util.List;

public interface EventService {
    EventFoolDto addEvent(EventAddDto eventAddDto, Long id);

    List<EventShortDto> findEvents(Long id, int from, int size);

}
