package ru.practicum.error.exceptions;

public class NotValidException extends RuntimeException {
    public NotValidException(final String message) {
        super(message);
    }
}