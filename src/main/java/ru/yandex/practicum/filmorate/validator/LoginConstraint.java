package ru.yandex.practicum.filmorate.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = LoginValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginConstraint {
    String message() default "Логин пользователя не может быть пустым и содержать пробелы.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}