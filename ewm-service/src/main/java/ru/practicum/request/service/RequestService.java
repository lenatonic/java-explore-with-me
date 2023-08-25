package ru.practicum.request.service;

import ru.practicum.request.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.request.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface RequestService {
    ParticipationRequestDto createRequest(Long userId, Long eventId, LocalDateTime time);

    ParticipationRequestDto canceledRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> findRequests(Long userId);

    List<ParticipationRequestDto> findRequestsByUsersEvent(Long idEvent, Long idUser);

    EventRequestStatusUpdateResultDto updateStatusRequests(Long idUser, Long idEvent,
                                                           EventRequestStatusUpdateRequestDto statusUpdateRequest);
}