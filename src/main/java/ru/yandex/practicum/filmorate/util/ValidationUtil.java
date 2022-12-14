package ru.yandex.practicum.filmorate.util;

import lombok.experimental.UtilityClass;
import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ValidationUtil {

    public static final String MIN_FILM_RELEASE_DATE_STR = "1895-12-28";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final LocalDate MIN_FILM_RELEASE_DATE = LocalDate.parse(MIN_FILM_RELEASE_DATE_STR, DATE_FORMATTER);
    public static final int MAX_DESCRIPTION_LENGTH = 200;

    public static final String LOGIN_PATTERN = "\\S+";

    public static String buildErrorString(FieldError error) {
        return String.format("Поле %s содержит ошибку: \"%s\"", error.getField(), error.getDefaultMessage());
    }

    public static String buildErrorMessage(List<FieldError> errors) {
        return errors.stream()
                .map(ValidationUtil::buildErrorString)
                .collect(Collectors.joining("\n"));
    }
}
