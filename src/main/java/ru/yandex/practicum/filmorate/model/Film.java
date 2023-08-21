package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.RequiredArgsConstructor;
import lombok.NonNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.filmorate.validator.ReleaseDateConstraint;

import java.time.LocalDate;
import java.util.Set;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class Film {

    private long id;

    @NotBlank(message = "Название фильма не может быть пустым.")
    @NonNull
    private String name;

    @Size(max = 200, message = "Максимальная длина описания фильма — 200 символов.")
    @NonNull
    private String description;

    @ReleaseDateConstraint
    @NonNull
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной.")
    @NonNull
    private double duration;

    @NonNull
    private Integer rate;

    @NonNull
    private Mpa mpa;

    private Set<Genres> genres;

    public Film() {
    }

    public Film(String name, String description, LocalDate releaseDate, double duration, Integer rate,
                Mpa mpa, Set<Genres> genres) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
        this.mpa = mpa;
        this.genres = genres;
    }

    public Film(long id, String name, String description, LocalDate releaseDate, double duration, Integer rate,
                Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
        this.mpa = mpa;
    }

}
