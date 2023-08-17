package ru.practicum.request.service;

import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto canceledRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> findRequests(Long userId);

    List<ParticipationRequestDto> findRequestsByUsersEvent(Long idEvent, Long idUser);

    EventRequestStatusUpdateResult updateStatusRequests(Long idUser, Long idEvent,
                                                        EventRequestStatusUpdateRequest statusUpdateRequest);
}