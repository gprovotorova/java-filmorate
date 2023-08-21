package ru.yandex.practicum.filmorate.exception;

public class NullPointerException extends RuntimeException {
    public NullPointerException(String message) {
        super(message);
    }

    public NullPointerException(String message, Throwable cause) {
        super(message, cause);
    }

    public NullPointerException(Throwable cause) {
        super(cause);
    }
}
