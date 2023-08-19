package ru.practicum.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFoolDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.EventState;
import ru.practicum.event.service.EventService;

import javax.validation.Valid;
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
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        List<EventFoolDto> ans = eventService.findEventsForAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
        log.info("Получение списка событий по параметрам поиска");
        return ans;
    }

    @PatchMapping("/events/{eventId}")
    public EventFoolDto updateEventForAdmin(@PathVariable Long eventId,
                                            @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        EventFoolDto ans = eventService.updateEventForAdmin(eventId, updateEventAdminRequest);
        log.info("Админ редактирует событие id ={}.", eventId);
        return ans;
    }
}