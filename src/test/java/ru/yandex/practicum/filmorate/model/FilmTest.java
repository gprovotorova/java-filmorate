package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmTest {
    private static final String DESCRIPTION_FOR_CHECK = "Тихиро с мамой и папой переезжает в новый дом. Заблудившись " +
            "по дороге, они оказываются в странном пустынном городе, где их ждет великолепный пир. Родители с " +
            "жадностью набрасываются на еду и к ужасу девочки превращаются в свиней, став пленниками злой колдуньи " +
            "Юбабы. Теперь, оказавшись одна среди волшебных существ и загадочных видений, Тихиро должна придумать, " +
            "как избавить своих родителей от чар коварной старухи.";

    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.usingContext().getValidator();

    @DisplayName("проверка названия фильма на пустоту")
    @Test
    void testFilmNameValidation() {
        Film film = new Film("", "Шедевр Хаяо Миядзаки.", LocalDate.of(2001, 7, 20), 125d);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Название фильма не может быть пустым.");
    }

    @DisplayName("проверка длины описания фильма")
    @Test
    void testDescriptionValidation() {
        Film film = new Film("Унесённые призраками", DESCRIPTION_FOR_CHECK, LocalDate.of(2001, 7, 20), 125d);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Максимальная длина описания фильма — 200 символов.");
    }

    @DisplayName("проверка даты релиза фильма")
    @Test
    void testReleaseDateValidation() {
        Film film = new Film("Унесённые призраками", "Шедевр Хаяо Миядзаки.", LocalDate.of(1800, 7, 20), 125d);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Дата релиза фильма не может быть раньше 28 декабря 1895 года.");
    }

    @DisplayName("проверка продолжительности фильма")
    @Test
    void testDurationValidation() {
        Film film = new Film("Унесённые призраками", "Шедевр Хаяо Миядзаки.", LocalDate.of(2001, 7, 20), -125d);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Продолжительность фильма должна быть положительной.");
    }
}