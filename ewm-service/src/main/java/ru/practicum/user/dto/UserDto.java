package ru.practicum.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserDto {

    Long id;

    @NotBlank
    @Size(min = 1, max = 55)
    @Email
    String email;

    @NotBlank
    @Size(min = 1, max = 100)
    String name;
}