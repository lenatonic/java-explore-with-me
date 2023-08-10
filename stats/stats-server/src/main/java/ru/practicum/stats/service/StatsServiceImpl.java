package ru.practicum.stats.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.mapper.StatsMapper;
import ru.practicum.stats.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StatsServiceImpl implements StatsService {
    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EndpointHitRepository hitRepository;

    @Autowired
    public StatsServiceImpl(EndpointHitRepository repository) {
        this.hitRepository = repository;
    }

    @Override
    public void addEndPointHit(EndpointHitDto endpointHitDto) {
        hitRepository.save(StatsMapper.toEndPointHit(endpointHitDto));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> findStats(String start, String end, List<String> uris, Boolean unique) {

        if (unique) {
            return hitRepository.findStatsUniqueIp(LocalDateTime.parse(start, format), LocalDateTime.parse(end, format), uris)
                    .stream().map(StatsMapper::toViewStatsDto).collect(Collectors.toList());
        }
        if (uris == null || uris.isEmpty()) {
            return hitRepository.findStatsWithoutUri(LocalDateTime.parse(start, format), LocalDateTime.parse(end, format))
                    .stream().map(StatsMapper::toViewStatsDto).collect(Collectors.toList());
        }
        return hitRepository.findStats(LocalDateTime.parse(start, format), LocalDateTime.parse(end, format), uris)
                .stream().map(StatsMapper::toViewStatsDto).collect(Collectors.toList());
    }
}