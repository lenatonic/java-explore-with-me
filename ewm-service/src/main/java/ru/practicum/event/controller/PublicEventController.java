package ru.practicum.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFoolDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventsSort;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/events")
public class PublicEventController {
    private final EventService eventService;

    @Autowired
    public PublicEventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventShortDto> findEventsForPublic(@RequestParam(required = false)String text,
                                                   @RequestParam(required = false) List<Long> categories,
                                                   @RequestParam(required = false) Boolean paid,
                                                   @RequestParam(required = false) String rangeStart,
                                                   @RequestParam(required = false) String rangeEnd,
                                                   @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                   @RequestParam(required = false) EventsSort sort,
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "10") Integer size,
                                                   HttpServletRequest request) {
        List<EventShortDto> ans = eventService.findEventsForPublic(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request);
        log.info("Получение списка событий по публичному поиску.");
        return ans;
    }

    @GetMapping("/{id}")
    public EventFoolDto findEventForPublic(@PathVariable Long id, HttpServletRequest request) {
        EventFoolDto ans = eventService.findEventForPublic(id, request);
        log.info("Получение cобытия id = {} по публичному поиску.", id);
        return ans;
    }
}