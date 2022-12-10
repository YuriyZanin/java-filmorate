package ru.yandex.practicum.filmorate.model;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public abstract class AbstractValidationTest {
    Validator validator;

    public AbstractValidationTest() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
}
