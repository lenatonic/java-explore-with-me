package ru.practicum.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewCompilationDto {
    private List<Long> events;

    private Boolean pinned;

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;
}