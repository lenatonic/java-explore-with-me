package ru.practicum.error;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ApiError {
    List<String> errors;
    String message;
    String reason;
    String status;
    String timestamp;

    public <T> ApiError(HttpStatus status, String reason, String message, List<T> errors, String timestamp) {
    }
}