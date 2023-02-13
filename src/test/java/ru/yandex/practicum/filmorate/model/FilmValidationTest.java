package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.filmorate.util.ValidationUtil.MIN_FILM_RELEASE_DATE;

public class FilmValidationTest extends AbstractValidationTest {

    @Test
    void shouldBeSuccessValidation() {
        Rating rating = new Rating(1L, "test");
        Film test = Film.builder().name("test").releaseDate(LocalDate.now()).duration(100).mpa(rating).build();
        Set<ConstraintViolation<Film>> violations = validator.validate(test);
        assertTrue(violations.isEmpty());

        Film minDateTest = Film.builder().name("test").releaseDate(MIN_FILM_RELEASE_DATE).duration(100).mpa(rating).build();
        violations = validator.validate(minDateTest);
        assertTrue(violations.isEmpty());

        Film descriptionTest = Film.builder().name("test").releaseDate(LocalDate.now()).duration(100).mpa(rating).build();
        descriptionTest.setDescription("t".repeat(200));
        violations = validator.validate(descriptionTest);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldBeFailedIfNameIsNull() {
        Rating rating = new Rating(1L, "test");
        Film test = Film.builder().name(null).releaseDate(LocalDate.now()).duration(100).mpa(rating).build();
        Set<ConstraintViolation<Film>> violations = validator.validate(test);
        assertEquals(2, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void shouldBeFailedIfNameIsBlank() {
        Rating rating = new Rating(1L, "test");
        Film test = Film.builder().name("   ").releaseDate(LocalDate.now()).duration(100).mpa(rating).build();
        Set<ConstraintViolation<Film>> violations = validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void shouldBeFailedIfDurationIsNotPositive() {
        Rating rating = new Rating(1L, "test");
        Film test = Film.builder().name("test").releaseDate(LocalDate.now()).duration(0).mpa(rating).build();
        Set<ConstraintViolation<Film>> violations = validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("duration")));

        test = Film.builder().name("test").releaseDate(LocalDate.now()).duration(-100).mpa(rating).build();
        violations = validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("duration")));
    }

    @Test
    void shouldBeFailedIfDescriptionOverSize() {
        Rating rating = new Rating(1L, "test");
        Film test = Film.builder().name("test").releaseDate(LocalDate.now()).duration(100).mpa(rating).build();
        test.setDescription("t".repeat(201));
        Set<ConstraintViolation<Film>> violations = validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void shouldBeFailedIfReleaseDateBeforeMin() {
        Rating rating = new Rating(1L, "test");
        LocalDate incorrectDate = MIN_FILM_RELEASE_DATE.minusDays(1);
        Film test = Film.builder().name("test").releaseDate(incorrectDate).duration(500).mpa(rating).build();
        Set<ConstraintViolation<Film>> violations = validator.validate(test);
        System.out.println(violations);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate")));
    }
}
