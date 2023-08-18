package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventShortDto {
    private Long id;

    private String annotation;

    private CategoryDto category;

    private int confirmedRequests;

    private String eventDate;

    private UserShortDto initiator;

    private boolean paid;

    private String title;

    private Long views;
}