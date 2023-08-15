package ru.practicum.request.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

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
    public ParticipationRequestDto createRequest(@PathVariable("userId") Long userId,
                                                 @RequestParam("eventId") Long eventId) {
        ParticipationRequestDto ans = requestService.createRequest(userId, eventId);
        log.info("Пользователь с id {}, создаёт запрос на участие в событии id {}.", userId, eventId);
        return ans;
    }

}
