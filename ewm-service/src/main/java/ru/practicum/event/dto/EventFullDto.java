package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.EventState;
import ru.practicum.location.Location;
import ru.practicum.user.dto.UserShortDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto extends EventShortDto {
    private String createdOn;
    private String description;
    private Location location;
    private int participantLimit;
    private String publishedOn;
    private boolean requestModeration;
    private EventState state;

    @Builder
    public EventFullDto(Long id, String annotation, CategoryDto category, int confirmedRequests, String eventDate,
                        UserShortDto initiator, boolean paid, String title, Long views, String createdOn,
                        String description, Location location, int participantLimit, String publishedOn,
                        boolean requestModeration, EventState state) {
        super(id, annotation, category, confirmedRequests, eventDate, initiator, paid, title, views);
        this.createdOn = createdOn;
        this.description = description;
        this.location = location;
        this.participantLimit = participantLimit;
        this.publishedOn = publishedOn;
        this.requestModeration = requestModeration;
        this.state = state;
    }
}