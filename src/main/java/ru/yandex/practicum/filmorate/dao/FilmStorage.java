package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    void save(Film film);

    void update(Film film);

    void delete(Film film);

    Optional<Film> getById(long id);

    List<Film> getAllFilms();

    List<Film> getTopFilms(int count);

    void deleteLike(User user, Film film);

    void addLike(User user, Film film);
}
