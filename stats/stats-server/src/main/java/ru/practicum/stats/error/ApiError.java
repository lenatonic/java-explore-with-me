package ru.practicum.stats.error;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Setter
@Getter
public class ApiError {
    List<String> errors;
    String message;
    String reason;
    String status;
    String timestamp;
}