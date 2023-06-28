package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.Data;
import ru.yandex.practicum.filmorate.validator.ReleaseDateConstraint;

import java.time.LocalDate;
import java.util.Map;

@Data
public class Film {
    private long id;
    @NotBlank(message = "Название фильма не может быть пустым.")
    private final String name;
    @Size(max = 200, message = "Максимальная длина описания фильма — 200 символов.")
    private final String description;
    @ReleaseDateConstraint
    private final LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной.")
    private final double duration;

}
