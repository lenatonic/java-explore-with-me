package ru.practicum.compilation.dto;

import lombok.*;
import ru.practicum.validation.ValidationGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewCompilationDto {
    private Set<Long> events;

    private Boolean pinned;

    @NotBlank(groups = ValidationGroup.AddCompilation.class)
    @Size(min = 1, max = 50)
    private String title;
}