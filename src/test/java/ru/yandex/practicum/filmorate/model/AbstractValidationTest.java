package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AbstractValidationTest {
    static Validator validator;

    @BeforeAll
    static void beforeAll() {
        // https://stackoverflow.com/questions/29069956/how-to-test-validation-annotations-of-a-class-using-junit
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void shouldThrowExceptionIfNull() {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(null));
    }

}
