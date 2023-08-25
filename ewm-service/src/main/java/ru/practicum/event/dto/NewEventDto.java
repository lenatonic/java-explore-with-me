package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.location.LocationDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @NotBlank
    private String eventDate;

    private LocationDto location;

    private boolean paid;

    private int participantLimit;

    private Boolean requestModeration;

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
}