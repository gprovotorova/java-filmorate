package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LoginValidator implements ConstraintValidator<LoginConstraint, String> {

    @Override
    public boolean isValid(String login, ConstraintValidatorContext context) {
        if (login.isBlank()) {
            return false;
        }
        for (int i = 0; i < login.length() - 1; i++) {
            if (login.substring(i).equals(" ")) {
                return false;
            }
        }
        return true;
    }
}
