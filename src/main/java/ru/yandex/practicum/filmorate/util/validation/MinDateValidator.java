package ru.yandex.practicum.filmorate.util.validation;

import ru.yandex.practicum.filmorate.util.ValidationUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class MinDateValidator implements ConstraintValidator<MinDate, LocalDate> {

    private LocalDate value;
    private String returnMessage;

    @Override
    public void initialize(MinDate constraintAnnotation) {
        this.value = LocalDate.parse(constraintAnnotation.value(), ValidationUtil.DATE_FORMATTER);
        this.returnMessage = constraintAnnotation.message().concat(this.value.format(ValidationUtil.DATE_FORMATTER));
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext ctx) {
        boolean valid = !localDate.isBefore(value);
        if (!valid) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate(returnMessage)
                    .addConstraintViolation();
        }
        return valid;
    }
}
