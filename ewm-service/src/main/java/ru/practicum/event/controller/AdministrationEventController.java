package ru.practicum.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequestDto;
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
    public List<EventFullDto> findEventsForAdmin(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size) {
        List<EventFullDto> ans = eventService.findEventsForAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
        log.info("Получение списка событий по параметрам поиска");
        return ans;
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEventForAdmin(@PathVariable Long eventId,
                                            @RequestBody @Valid UpdateEventAdminRequestDto updateEventAdminRequestDto) {
        EventFullDto ans = eventService.updateEventForAdmin(eventId, updateEventAdminRequestDto);
        log.info("Админ редактирует событие id ={}.", eventId);
        return ans;
    }
}