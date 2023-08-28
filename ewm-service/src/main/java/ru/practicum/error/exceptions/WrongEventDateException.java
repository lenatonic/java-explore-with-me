package ru.practicum.error.exceptions;

public class WrongEventDateException extends RuntimeException {
    public WrongEventDateException(final String message) {
        super(message);
    }
}