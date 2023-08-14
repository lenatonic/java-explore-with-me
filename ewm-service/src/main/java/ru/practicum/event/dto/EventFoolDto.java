package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.Location;
import ru.practicum.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventFoolDto {
    private Long id;

    @NotBlank
    private String annotation;

    private CategoryDto category;

    private int confirmedRequests;

    private String createdOn;

    private String description;

    @NotBlank
    private String eventDate;

    @NotBlank
    private UserShortDto initiator;

    @NotBlank
    private Location location;

    @NotBlank
    private boolean paid;

    private int participantLimit;

    private String publishedOn;

    private boolean requestModeration;

    private EventState state;

    @NotBlank
    private String title;

    private int views;
}