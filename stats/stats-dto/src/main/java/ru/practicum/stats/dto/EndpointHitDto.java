package ru.practicum.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class EndpointHitDto {
    private Long id;

    @NotBlank
    @NotNull
    private String app;

    @NotBlank
    @NotNull
    private String uri;

    @NotBlank
    @NotNull
    private String ip;

    private String timestamp;
}