package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.filmorate.util.ValidationUtil.*;

public class FilmValidationTest extends AbstractValidationTest{

    @Test
    void shouldBeSuccessValidation() {
        Set<ConstraintViolation<Film>> violations = validator.validate(new Film("test", LocalDate.now(), 100));
        assertTrue(violations.isEmpty());

        violations = validator.validate(new Film("test", MIN_FILM_RELEASE_DATE, 100));
        assertTrue(violations.isEmpty());

        Film descriptionTest = new Film("test", LocalDate.now(), 100);
        descriptionTest.setDescription("t".repeat(200));
        violations = validator.validate(descriptionTest);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldBeFailedIfNameIsNull() {
        Set<ConstraintViolation<Film>> violations = validator.validate(new Film(null, LocalDate.now(), 100));
        assertEquals(2, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")
                && v.getMessage().equals("не должно равняться null")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")
                && v.getMessage().equals("не должно быть пустым")));
    }

    @Test
    void shouldBeFailedIfNameIsBlank() {
        Set<ConstraintViolation<Film>> violations = validator.validate(new Film("   ", LocalDate.now(), 100));
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")
                && v.getMessage().equals("не должно быть пустым")));
    }

    @Test
    void shouldBeFailedIfDurationIsNotPositive() {
        Set<ConstraintViolation<Film>> violations = validator.validate(new Film("test", LocalDate.now(), 0));
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("duration")
                && v.getMessage().equals("должно быть больше 0")));

        violations = validator.validate(new Film("test", LocalDate.now(), -100));
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("duration")
                && v.getMessage().equals("должно быть больше 0")));
    }

    @Test
    void shouldBeFailedIfDescriptionOverSize() {
        Film test = new Film("test", LocalDate.now(), 100);
        test.setDescription("t".repeat(201));
        Set<ConstraintViolation<Film>> violations = validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")
                && v.getMessage().equals("размер должен находиться в диапазоне от 0 до " + MAX_DESCRIPTION_LENGTH)));
    }

    @Test
    void shouldBeFailedIfReleaseDateBeforeMin() {
        Set<ConstraintViolation<Film>> violations = validator.validate(
                new Film("test", MIN_FILM_RELEASE_DATE.minusDays(1), 500));
        System.out.println(violations);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate")
                && v.getMessage().equals("дата релиза должна быть не раньше " + MIN_FILM_RELEASE_DATE_STR)));
    }
}
