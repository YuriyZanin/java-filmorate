package ru.yandex.practicum.filmorate.util;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

@UtilityClass
public class ValidationUtils {
    public static void validate(User user, Logger log) {
        if (user == null) {
            String message = "Отсутствует тело зарпоса";
            log.error(message);
            throw new ValidationException(message);
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            String message = "Адрес электронной почты не может быть пустым.";
            log.error(message);
            throw new ValidationException(message);
        }
        if (!user.getEmail().contains("@")) {
            String message = "Некорректный адрес электронной почты.";
            log.error(message);
            throw new ValidationException(message);
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            String message = "Логин не должен быть пустым или содержать пробелы.";
            log.error(message);
            throw new ValidationException(message);
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            String message = "Дата рождения не может быть в будущем.";
            log.error(message);
            throw new ValidationException(message);
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public static void validate(Film film, Logger log) {
        if (film == null) {
            String message = "Отсутствует тело зарпоса";
            log.error(message);
            throw new ValidationException(message);
        }
        if (film.getName() == null || film.getName().isBlank()) {
            String message = "Название фильма не может быть пустым.";
            log.error(message);
            throw new ValidationException(message);
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            String message = "Максимальная длинна описания не должна превышать 200 символов.";
            log.error(message);
            throw new ValidationException(message);
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            String message = "Дата релиза должна быть не раньше 28 декабря 1895 года.";
            log.error(message);
            throw new ValidationException(message);
        }
        if (film.getDuration().isZero() || film.getDuration().isNegative()) {
            String message = "Продолжительность фильма должна быть положительной.";
            log.error(message);
            throw new ValidationException(message);
        }
    }
}
