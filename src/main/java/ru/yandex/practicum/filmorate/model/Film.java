package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.Data;
import ru.yandex.practicum.filmorate.validator.ReleaseDateConstraint;

import java.time.LocalDate;

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
    private Genre genre;
    private MotionPictureAssociation mpa;

    public Film(String name, String description, LocalDate releaseDate, double duration, Genre genre, MotionPictureAssociation mpa) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genre = genre;
        this.mpa = mpa;
    }
}
