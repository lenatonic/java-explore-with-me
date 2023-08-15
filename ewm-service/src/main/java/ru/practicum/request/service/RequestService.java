package ru.practicum.request.service;

import ru.practicum.request.dto.ParticipationRequestDto;

public interface RequestService {
    ParticipationRequestDto createRequest(Long userId, Long eventId);
}
