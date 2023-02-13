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
        User test = User.builder().email("test@email.com").login("test").birthday(LocalDate.now()).build();
        Set<ConstraintViolation<User>> violations = validator.validate(test);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldBeFailedIfEmailIsIncorrect() {
        User test = User.builder().email("incorrectMail@").login("login").birthday(LocalDate.now()).build();
        Set<ConstraintViolation<User>> violations = validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldBeFailedIfEmailIsNull() {
        User test = User.builder().email(null).login("login").birthday(LocalDate.now()).build();
        Set<ConstraintViolation<User>> violations = validator.validate(test);
        assertEquals(2, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldBeFailedIfEmailIsEmpty() {
        User test = User.builder().email("").login("login").birthday(LocalDate.now()).build();
        Set<ConstraintViolation<User>> violations = validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));

        test = User.builder().email("   ").login("login").birthday(LocalDate.now()).build();
        violations = validator.validate(test);
        assertEquals(2, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldBeFailedIfLoginIsNull() {
        User test = User.builder().email("test@mail.com").login(null).birthday(LocalDate.now()).build();
        Set<ConstraintViolation<User>> violations = validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")));
    }

    @Test
    void shouldBeFailedIfLoginWithSpaces() {
        User test = User.builder().email("test@mail.com").login("a b").birthday(LocalDate.now()).build();
        Set<ConstraintViolation<User>> violations = validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")));

        test = User.builder().email("test@mail.com").login(" ab ").birthday(LocalDate.now()).build();
        violations = validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")));

        test = User.builder().email("test@mail.com").login("  ").birthday(LocalDate.now()).build();
        violations = validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")));
    }

    @Test
    void shouldBeFailedIfLoginIsEmpty() {
        User test = User.builder().email("test@mail.com").login("").birthday(LocalDate.now()).build();
        Set<ConstraintViolation<User>> violations = validator.validate(test);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldBeFailedIfBirthDayIsFuture() {
        User test = User.builder().email("test@mail.com").login("login").birthday(LocalDate.now().plusDays(1)).build();
        Set<ConstraintViolation<User>> violations =
                validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("birthday")));
    }
}
