package ru.practicum.request.service;

import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto canceledRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> findRequests(Long userId);
}
