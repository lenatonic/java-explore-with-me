package ru.practicum.stats.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.stats.dto.EndpointHitDto;

import java.util.List;
import java.util.Map;

public class StatsClient extends BaseClient {

    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> addHit(EndpointHitDto endpointHitDto) {
        return post("/hit", endpointHitDto);
    }

    public ResponseEntity<Object> findStats(String start, String end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end);

        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters, uris, unique);
    }
}