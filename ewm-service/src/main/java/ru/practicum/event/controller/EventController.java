package ru.practicum.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequestDto;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.request.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;
import ru.practicum.util.ValidationGroup;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@Validated
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
    @Validated(ValidationGroup.AddEvent.class)
    @PostMapping("/{userId}/events")
    public EventFullDto addEvent(@Valid @RequestBody NewEventDto newEventDto,
                                 @PathVariable(name = "userId") Long id) {
        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
        }
        EventFullDto ans = eventService.addEvent(newEventDto, id);
        log.info("Пользователь id ={} создал новое событие id = {}", id, ans.getId());
        return ans;
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> findEvents(@PathVariable(name = "userId") Long id,
                                          @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                          @RequestParam(defaultValue = "10") @Positive int size) {
        List<EventShortDto> ans = eventService.findEvents(id, from, size);
        log.info("Список событий пользователя id {}", id);
        return ans;
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto findEvent(@PathVariable(name = "userId") Long idUser,
                                  @PathVariable(name = "eventId") Long idEvent) {
        EventFullDto ans = eventService.findEvent(idUser, idEvent);
        log.info("Получение данных по событию id = {}", idEvent);
        return ans;
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable(name = "userId") Long idUser,
                                    @PathVariable(name = "eventId") Long idEven,
                                    @RequestBody @Valid UpdateEventUserRequestDto updateEventUserRequestDto) {
        EventFullDto ans = eventService.updateEvent(idUser, idEven, updateEventUserRequestDto);
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
    public EventRequestStatusUpdateResultDto updateStatusRequests(@PathVariable(name = "userId") Long idUser,
                                                                  @PathVariable(name = "eventId") Long idEvent,
                                                                  @RequestBody EventRequestStatusUpdateRequestDto statusUpdateRequest) {
        EventRequestStatusUpdateResultDto ans = requestService.updateStatusRequests(idUser, idEvent, statusUpdateRequest);
        log.info("Пользователь id = {} меняет статус заявок на событие id = {}.", idUser, idEvent);
        return ans;
    }
}