package ru.yandex.practicum.filmorate.exeption;

public class AlreadyExistException extends RuntimeException {
    public AlreadyExistException(String s) {
        super(s);
    }
}
