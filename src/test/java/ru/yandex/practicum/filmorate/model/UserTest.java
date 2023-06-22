package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.Validator;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.usingContext().getValidator();

    @DisplayName("проверка корректности адреса электронной почты")
    @Test
    void testEmailValidation() {
        User user = new User("hello@", "hello", "Mary Ivanova", LocalDate.of(2000, 8, 5));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Некорректный адрес электронной почты.");

        user = new User("", "hello", "Mary Ivanova", LocalDate.of(2000, 8, 5));

        violations = validator.validate(user);
        assertEquals(1, violations.size(), "Адрес электронной почты не может быть пустой.");
    }

    @DisplayName("проверка логина на пустоту")
    @Test
    void testLoginValidation() {
        User user = new User("hello@mail.ru", "", "Mary Ivanova", LocalDate.of(2000, 8, 5));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Логин пользователя пустой");
    }

    @DisplayName("проверка даты рождения")
    @Test
    void testBirthdayValidation() {
        User user = new User("hello@mail.ru", "hello", "Mary Ivanova", LocalDate.of(2200, 8, 5));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Дата рождения позже " + LocalDate.now());
    }
}