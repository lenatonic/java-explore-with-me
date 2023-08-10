package ru.practicum.stats.service;

import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import java.util.List;

public interface StatsService {
    void addEndPointHit(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> findStats(String start, String end, List<String> uris, Boolean unique);
}