package ru.practicum.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventAddDto;
import ru.practicum.event.dto.EventFoolDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class EventController {
    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{userId}/events")
    public EventFoolDto addEvent(@Valid @RequestBody EventAddDto eventAddDto,
                                 @PathVariable(name = "userId") Long id) {
        EventFoolDto ans = eventService.addEvent(eventAddDto, id);
        log.debug("Пользователь id ={} создал новое событие id = ", id, ans.getId());
        return ans;
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> findEvents(@PathVariable(name = "userId") Long id,
                                          @RequestParam(defaultValue = "0") int from,
                                          @RequestParam(defaultValue = "10") int size) {
        List<EventShortDto> ans = eventService.findEvents(id, from, size);
        log.debug("Список событий пользователя id {}", id);
        return ans;
    }
}
