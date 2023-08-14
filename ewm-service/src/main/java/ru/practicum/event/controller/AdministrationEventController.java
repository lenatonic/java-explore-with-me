package ru.practicum.event.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.service.EventService;

@RestController
public class AdministrationEventController {
    private final EventService eventService;

    @Autowired
    public AdministrationEventController(EventService eventService) {
        this.eventService = eventService;
    }
}