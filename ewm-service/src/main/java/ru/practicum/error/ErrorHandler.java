package ru.practicum.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.error.exceptions.NotFoundException;
import ru.practicum.error.exceptions.NotValidException;
import ru.practicum.error.exceptions.WrongEventDateException;
import ru.practicum.util.Patterns;

import javax.validation.ConstraintViolationException;
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
    public ApiError handleNotValidException(NotValidException exception) {
        log.error("error", exception);
        return ApiError.builder()
                .errors(Collections.singletonList(Arrays.toString(exception.getStackTrace())))
                .reason("bad request")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)))
                .message(exception.getMessage())
                .status(HttpStatus.BAD_REQUEST.getReasonPhrase().toUpperCase())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(Throwable exception, HttpStatus status) {
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
        log.error("error", e);
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
        log.error("error", e);
        return ApiError.builder()
                .errors(Collections.singletonList(Arrays.toString(e.getStackTrace())))
                .reason("conflict")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)))
                .message(e.getMessage())
                .status(HttpStatus.CONFLICT.getReasonPhrase().toUpperCase())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleWrongEventDateException(WrongEventDateException e) {
        log.error("error", e);
        return ApiError.builder()
                .errors(Collections.singletonList(Arrays.toString(e.getStackTrace())))
                .reason("conflict")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)))
                .message(e.getMessage())
                .status(HttpStatus.CONFLICT.getReasonPhrase().toUpperCase())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolationException(final ConstraintViolationException e) {
        log.error("error", e);
        return ApiError.builder()
                .errors(Collections.singletonList(Arrays.toString(e.getStackTrace())))
                .reason("Not valid")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)))
                .message(e.getMessage())
                .status(HttpStatus.BAD_REQUEST.getReasonPhrase().toUpperCase())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ApiError handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.error("error", e);
        return ApiError.builder()
                .errors(Collections.singletonList(Arrays.toString(e.getStackTrace())))
                .reason("bad request")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN)))
                .message(e.getMessage())
                .status(HttpStatus.BAD_REQUEST.getReasonPhrase().toUpperCase())
                .build();
    }
}