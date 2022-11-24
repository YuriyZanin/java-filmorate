package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.filmorate.util.ValidationUtil.MIN_FILM_RELEASE_DATE;
import static ru.yandex.practicum.filmorate.util.ValidationUtil.MIN_FILM_RELEASE_DATE_STR;

public class FilmValidationTest extends AbstractValidationTest {

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
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void shouldBeFailedIfNameIsBlank() {
        Set<ConstraintViolation<Film>> violations = validator.validate(new Film("   ", LocalDate.now(), 100));
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void shouldBeFailedIfDurationIsNotPositive() {
        Set<ConstraintViolation<Film>> violations = validator.validate(new Film("test", LocalDate.now(), 0));
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("duration")));

        violations = validator.validate(new Film("test", LocalDate.now(), -100));
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("duration")));
    }

    @Test
    void shouldBeFailedIfDescriptionOverSize() {
        Film test = new Film("test", LocalDate.now(), 100);
        test.setDescription("t".repeat(201));
        Set<ConstraintViolation<Film>> violations = validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
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
