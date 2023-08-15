package ru.practicum.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.ParticipationRequest;

@UtilityClass
public class RequestMapper {
    public ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return ParticipationRequestDto.builder()
                .created(participationRequest.getCreated())
                .id(participationRequest.getId())
                .status(participationRequest.getStatus())
                .requester(participationRequest.getRequester().getId())
                .event(participationRequest.getEvent().getId())
                .build();
    }
}