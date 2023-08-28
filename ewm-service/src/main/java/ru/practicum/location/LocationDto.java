package ru.practicum.location;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LocationDto {
    private Long id;
    private float lat;
    private float lon;
}
