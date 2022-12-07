package ru.yandex.practicum.filmorate.util.exeption;

public class AlreadyExistException extends RuntimeException {
    public AlreadyExistException(String s) {
        super(s);
    }
}
