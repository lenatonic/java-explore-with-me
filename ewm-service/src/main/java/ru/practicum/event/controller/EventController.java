package ru.practicum.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.EventFoolDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
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
    public EventFoolDto addEvent(@Valid @RequestBody NewEventDto newEventDto,
                                 @PathVariable(name = "userId") Long id) {
        if(newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
        }
        EventFoolDto ans = eventService.addEvent(newEventDto, id);
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

    @GetMapping("/{userId}/events/{eventId}")
    public EventFoolDto findEvent(@PathVariable(name = "userId") Long idUser,
                                         @PathVariable(name = "eventId") Long idEvent) {
        EventFoolDto ans = eventService.findEvent(idUser, idEvent);
        log.debug("Получение данных по событию id = {}", idEvent);
        return ans;
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFoolDto updateEvent(@PathVariable(name = "userId") Long idUser,
                                    @PathVariable(name = "eventId") Long idEven,
                                    @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        EventFoolDto ans = eventService.updateEvent(idUser, idEven, updateEventUserRequest);
        log.debug("Пользователь id = {}, изменил событие id = {}", idUser, idEven);
        return ans;
    }
}
