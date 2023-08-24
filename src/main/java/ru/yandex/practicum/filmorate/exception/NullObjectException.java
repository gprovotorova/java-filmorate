package ru.yandex.practicum.filmorate.exception;

public class NullObjectException extends RuntimeException {
    public NullObjectException(String message) {
        super(message);
    }

    public NullObjectException(String message, Throwable cause) {
        super(message, cause);
    }

    public NullObjectException(Throwable cause) {
        super(cause);
    }
}
