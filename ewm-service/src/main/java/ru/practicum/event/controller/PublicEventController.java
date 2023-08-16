package ru.practicum.event.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.dto.EventFoolDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;

@RestController
@RequestMapping("/events")
public class PublicEventController {
    private final EventService eventService;

    @Autowired
    public PublicEventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public EventShortDto findEventsForPublic() {
return null;
    }
}

