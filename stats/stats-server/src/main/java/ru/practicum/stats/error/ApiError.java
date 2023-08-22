package ru.practicum.stats.error;

import lombok.Builder;

import java.util.List;

@Builder
public class ApiError {
    List<String> errors;
    String message;
    String reason;
    String status;
    String timestamp;
}