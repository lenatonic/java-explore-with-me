package ru.practicum.request.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequestStatusUpdateResult {
    private ParticipationRequestDto confirmedRequests;
    private ParticipationRequestDto rejectedRequests;
}