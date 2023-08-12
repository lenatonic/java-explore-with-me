package ru.practicum.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.error.exceptions.NotFoundException;
import ru.practicum.util.Patterns;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;


@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException exception,
                                                          HttpStatus status) {
        log.info("400{}", exception.getMessage());
        return new ApiError(status, "Incorrectly made request", exception.getMessage(), Collections.emptyList(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(Exception exception, HttpStatus status) {
        log.error("error", exception);
        StringWriter out = new StringWriter();
        exception.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();
        return new ApiError(status, "Error", exception.getMessage(), Collections.singletonList(stackTrace),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(NotFoundException e) {
        return ApiError.builder()
                .errors(Collections.singletonList(Arrays.toString(e.getStackTrace())))
                .reason("Not found")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)))
                .message(e.getMessage())
                .status(HttpStatus.NOT_FOUND.getReasonPhrase().toUpperCase())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return ApiError.builder()
                .errors(Collections.singletonList(Arrays.toString(e.getStackTrace())))
                .reason("conflict")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)))
                .message(e.getMessage())
                .status(HttpStatus.CONFLICT.getReasonPhrase().toUpperCase())
                .build();
    }

    static String findStackTrace(Throwable throwable) {
        StringWriter out = new StringWriter();
        throwable.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();
        return stackTrace;
    }
}