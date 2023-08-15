package ru.practicum.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.dto.EventFoolDto;
import ru.practicum.event.model.EventState;
import ru.practicum.event.service.EventService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin")
public class AdministrationEventController {
    private final EventService eventService;

    @Autowired
    public AdministrationEventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public List<EventFoolDto> findEventsForAdmin(
            @RequestParam List<Long> users,
            @RequestParam List<EventState> states,
            @RequestParam List<Long> categories,
            @RequestParam String rangeStart,
            @RequestParam String rangeEnd,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        List<EventFoolDto> ans = eventService.findEventsForAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
        log.debug("Получение списка событий по параметрам поиска");
        return ans;
    }
}
