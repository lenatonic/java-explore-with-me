package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.location.Location;

import javax.validation.constraints.Size;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateEventUserRequest {

    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    private String eventDate;

    private Location location;

    private Boolean paid;

    private int participantLimit;

    private Boolean requestModeration;

    private StateActionForUser stateAction;

    @Size(min = 3, max = 120)
    private String title;
}