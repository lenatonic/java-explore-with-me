package ru.practicum.request.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users/{userId}/requests")
public class RequestController {

    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable("userId") Long userId,
                                                 @RequestParam("eventId") Long eventId) {
        ParticipationRequestDto ans = requestService.createRequest(userId, eventId);
        log.info("Пользователь с id {}, создаёт запрос на участие в событии id {}.", userId, eventId);
        return ans;
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto canceledRequest(@PathVariable("userId") Long userId,
                                                   @PathVariable("requestId") Long requestId) {
        ParticipationRequestDto ans = requestService.canceledRequest(userId, requestId);
        log.info("Пользователь id = {}, отменяет свою заявку id = {}.", userId, requestId);
        return ans;
    }

    @GetMapping
    public List<ParticipationRequestDto> findRequests(@PathVariable("userId") Long userId) {
        List<ParticipationRequestDto> ans = requestService.findRequests(userId);
        log.info("Получения списка запросов пользователя id = {}.", userId);
        return ans;
    }
}