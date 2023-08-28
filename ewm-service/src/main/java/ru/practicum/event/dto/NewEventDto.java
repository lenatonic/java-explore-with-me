package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.location.LocationDto;
import ru.practicum.validation.ValidationGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {

    @NotBlank(groups = ValidationGroup.AddEvent.class)
    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @NotBlank(groups = ValidationGroup.AddEvent.class)
    @Size(min = 20, max = 7000)
    private String description;

    @NotBlank(groups = ValidationGroup.AddEvent.class)
    private String eventDate;

    private LocationDto location;

    private Boolean paid;

    private int participantLimit;

    private Boolean requestModeration;

    @NotBlank(groups = ValidationGroup.AddEvent.class)
    @Size(min = 3, max = 120)
    private String title;
}