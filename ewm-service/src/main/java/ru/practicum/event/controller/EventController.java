package ru.practicum.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFoolDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class EventController {
    private final EventService eventService;
    private final RequestService requestService;

    @Autowired
    public EventController(EventService eventService, RequestService requestService) {
        this.eventService = eventService;
        this.requestService = requestService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{userId}/events")
    public EventFoolDto addEvent(@Valid @RequestBody NewEventDto newEventDto,
                                 @PathVariable(name = "userId") Long id) {
        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
        }
        EventFoolDto ans = eventService.addEvent(newEventDto, id);
        log.info("Пользователь id ={} создал новое событие id = ", id, ans.getId());
        return ans;
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> findEvents(@PathVariable(name = "userId") Long id,
                                          @RequestParam(defaultValue = "0") int from,
                                          @RequestParam(defaultValue = "10") int size) {
        List<EventShortDto> ans = eventService.findEvents(id, from, size);
        log.info("Список событий пользователя id {}", id);
        return ans;
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFoolDto findEvent(@PathVariable(name = "userId") Long idUser,
                                  @PathVariable(name = "eventId") Long idEvent) {
        EventFoolDto ans = eventService.findEvent(idUser, idEvent);
        log.info("Получение данных по событию id = {}", idEvent);
        return ans;
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFoolDto updateEvent(@PathVariable(name = "userId") Long idUser,
                                    @PathVariable(name = "eventId") Long idEven,
                                    @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        EventFoolDto ans = eventService.updateEvent(idUser, idEven, updateEventUserRequest);
        log.info("Пользователь id = {}, изменил событие id = {}", idUser, idEven);
        return ans;
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> findRequestsByUsersEvent(@PathVariable(name = "userId") Long idUser,
                                                                  @PathVariable(name = "eventId") Long idEvent) {
        List<ParticipationRequestDto> ans = requestService.findRequestsByUsersEvent(idEvent, idUser);
        log.info("Получение списка запросов на событие id = {}, пользователя id = {}", idEvent, idUser);
        return ans;
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatusRequests(@PathVariable(name = "userId") Long idUser,
                                                                     @PathVariable(name = "eventId") Long idEvent,
                                                                     @RequestBody EventRequestStatusUpdateRequest statusUpdateRequest) {
        EventRequestStatusUpdateResult ans = requestService.updateStatusRequests(idUser, idEvent, statusUpdateRequest);
        log.info("Пользователь id = {} меняет статус заявок на событие id = {}.", idUser, idEvent);
        return ans;
    }
}