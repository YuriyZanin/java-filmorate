package ru.yandex.practicum.filmorate.util.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = MinDateValidator.class)
public @interface MinDate {
    String message() default "дата релиза должна быть не раньше ";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String value();
}
