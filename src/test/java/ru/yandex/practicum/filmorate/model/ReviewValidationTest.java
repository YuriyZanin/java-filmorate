package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.dto.ReviewDto;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ReviewValidationTest extends AbstractValidationTest {

    @Test
    void shouldBeSuccessValidation() {
        ReviewDto test = ReviewDto.builder().content("test").isPositive(true).userId(1L).filmId(1L).build();
        Set<ConstraintViolation<ReviewDto>> violations = validator.validate(test);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldBeFailedIfContentIsNull() {
        ReviewDto test = ReviewDto.builder().content(null).isPositive(true).userId(1L).filmId(1L).build();
        Set<ConstraintViolation<ReviewDto>> violations = validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("content")));
    }

    @Test
    void shouldBeFailedIfContentIsEmpty() {
        ReviewDto test = ReviewDto.builder().content("").isPositive(true).userId(1L).filmId(1L).build();
        Set<ConstraintViolation<ReviewDto>> violations = validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("content")));
    }

    @Test
    void shouldBeFailedIfContentIsBlank() {
        ReviewDto test = ReviewDto.builder().content("    ").isPositive(true).userId(1L).filmId(1L).build();
        Set<ConstraintViolation<ReviewDto>> violations = validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("content")));
    }

    @Test
    void shouldBeFailedIfUserIdIsNegative() {
        ReviewDto test = ReviewDto.builder().content("test").isPositive(true).userId(-1L).filmId(1L).build();
        Set<ConstraintViolation<ReviewDto>> violations = validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("userId")));
    }

    @Test
    void shouldBeFailedIfUserIdIsNull() {
        ReviewDto test = ReviewDto.builder().content("test").isPositive(true).userId(null).filmId(1L).build();
        Set<ConstraintViolation<ReviewDto>> violations = validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("userId")));
    }

    @Test
    void shouldBeFailedIfFilmIdIsNegative() {
        ReviewDto test = ReviewDto.builder().content("test").isPositive(true).userId(1L).filmId(-1L).build();
        Set<ConstraintViolation<ReviewDto>> violations = validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("filmId")));
    }

    @Test
    void shouldBeFailedIfFilmIdIsNull() {
        ReviewDto test = ReviewDto.builder().content("test").isPositive(true).userId(1L).filmId(null).build();
        Set<ConstraintViolation<ReviewDto>> violations = validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("filmId")));
    }

    @Test
    void shouldBeFailedIfIsPositiveNull() {
        ReviewDto test = ReviewDto.builder().content("test").isPositive(null).userId(1L).filmId(1L).build();
        Set<ConstraintViolation<ReviewDto>> violations = validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("isPositive")));
    }
}
