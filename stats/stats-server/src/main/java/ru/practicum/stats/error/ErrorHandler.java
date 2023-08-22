package ru.practicum.stats.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.stats.error.exceptions.NotValidException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;


@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleNotValidException(NotValidException e) {
        log.info("400{}", e.getMessage());
        return ApiError.builder()
                .errors(Collections.singletonList(Arrays.toString(e.getStackTrace())))
                .reason("Not found")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .message(e.getMessage())
                .status(HttpStatus.NOT_FOUND.getReasonPhrase().toUpperCase())
                .build();
    }
}