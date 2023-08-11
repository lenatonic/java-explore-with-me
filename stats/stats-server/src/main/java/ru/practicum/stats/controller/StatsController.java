package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.service.StatsService;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {

    private final StatsService service;

    @PostMapping("/hit")
    public void addEndPointHit(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        service.addEndPointHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> findStats(@RequestParam String start,
                                        @RequestParam String end,
                                        @RequestParam(required = false) List<String> uris,
                                        @RequestParam(defaultValue = "false") boolean unique) {
        return service.findStats(start, end, uris, unique);
    }
}