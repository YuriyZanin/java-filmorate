package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserValidationTest extends AbstractValidationTest {

    @Test
    void shouldBeSuccessValidation() {
        Set<ConstraintViolation<User>> violations = validator.validate(new User("test@email.com", "test", LocalDate.now()));
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldBeFailedIfEmailIsIncorrect() {
        Set<ConstraintViolation<User>> violations = validator.validate(new User("incorrectMail@", "login", LocalDate.now()));
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldBeFailedIfEmailIsNull() {
        Set<ConstraintViolation<User>> violations = validator.validate(new User(null, "login", LocalDate.now()));
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldBeFailedIfLoginIsNull() {
        Set<ConstraintViolation<User>> violations = validator.validate(new User("test@mail.com", null, LocalDate.now()));
        assertEquals(2, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")));
    }

    @Test
    void shouldBeFailedIfLoginIsBlank() {
        Set<ConstraintViolation<User>> violations = validator.validate(new User("test@mail.com", "   ", LocalDate.now()));
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")));
    }

    @Test
    void shouldBeFailedIfBirthDayIsFuture() {
        Set<ConstraintViolation<User>> violations =
                validator.validate(new User("test@mail.com", "login", LocalDate.now().plusDays(1)));
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("birthday")));
    }
}
