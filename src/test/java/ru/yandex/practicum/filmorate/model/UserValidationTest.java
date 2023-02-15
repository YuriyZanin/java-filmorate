package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.dto.UserDto;

import javax.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserValidationTest extends AbstractValidationTest {

    @Test
    void shouldBeSuccessValidation() {
        UserDto test = UserDto.builder().email("test@email.com").login("test").birthday(LocalDate.now()).build();
        Set<ConstraintViolation<UserDto>> violations = validator.validate(test);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldBeFailedIfEmailIsIncorrect() {
        UserDto test = UserDto.builder().email("incorrectMail@").login("login").birthday(LocalDate.now()).build();
        Set<ConstraintViolation<UserDto>> violations = validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldBeFailedIfEmailIsNull() {
        UserDto test = UserDto.builder().email(null).login("login").birthday(LocalDate.now()).build();
        Set<ConstraintViolation<UserDto>> violations = validator.validate(test);
        assertEquals(2, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldBeFailedIfEmailIsEmpty() {
        UserDto test = UserDto.builder().email("").login("login").birthday(LocalDate.now()).build();
        Set<ConstraintViolation<UserDto>> violations = validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));

        test = UserDto.builder().email("   ").login("login").birthday(LocalDate.now()).build();
        violations = validator.validate(test);
        assertEquals(2, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldBeFailedIfLoginIsNull() {
        UserDto test = UserDto.builder().email("test@mail.com").login(null).birthday(LocalDate.now()).build();
        Set<ConstraintViolation<UserDto>> violations = validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")));
    }

    @Test
    void shouldBeFailedIfLoginWithSpaces() {
        UserDto test = UserDto.builder().email("test@mail.com").login("a b").birthday(LocalDate.now()).build();
        Set<ConstraintViolation<UserDto>> violations = validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")));

        test = UserDto.builder().email("test@mail.com").login(" ab ").birthday(LocalDate.now()).build();
        violations = validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")));

        test = UserDto.builder().email("test@mail.com").login("  ").birthday(LocalDate.now()).build();
        violations = validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")));
    }

    @Test
    void shouldBeFailedIfLoginIsEmpty() {
        UserDto test = UserDto.builder().email("test@mail.com").login("").birthday(LocalDate.now()).build();
        Set<ConstraintViolation<UserDto>> violations = validator.validate(test);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldBeFailedIfBirthDayIsFuture() {
        UserDto test = UserDto.builder().email("test@mail.com").login("login").birthday(LocalDate.now().plusDays(1)).build();
        Set<ConstraintViolation<UserDto>> violations =
                validator.validate(test);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("birthday")));
    }
}
