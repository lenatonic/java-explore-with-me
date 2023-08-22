package ru.practicum.stats.error.exceptions;

public class NotValidException extends RuntimeException {
    public NotValidException(final String message) {
        super(message);
    }
}