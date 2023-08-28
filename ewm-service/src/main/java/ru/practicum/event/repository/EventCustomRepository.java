package ru.practicum.event.repository;

import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;

import java.util.List;

public interface EventCustomRepository {
    List<Event> findEventsForPublic(String text, List<Long> categories, Boolean paid,
                                    String start, String end, int from, int size);

    List<Event> findEventsForAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                   String rangeStart, String rangeEnd, int from, int size);
}