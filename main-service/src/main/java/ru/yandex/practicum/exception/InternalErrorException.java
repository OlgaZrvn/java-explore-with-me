package ru.yandex.practicum.exception;

public class InternalErrorException extends InternalError {

    public InternalErrorException(String message) {
        super(message);
    }
}