package ru.practicum.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class EndpointHitDto {
    private Long id;

    @NotBlank
    @Size(min = 1, max = 255)
    private String app;

    @NotBlank
    @Size(min = 1, max = 255)
    private String uri;

    @NotBlank
    @Size(min = 1, max = 50)
    private String ip;

    private String timestamp;
}