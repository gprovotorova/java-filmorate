package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Optional<Film> save(Optional<Film> film);

    Optional<Film> update(Optional<Film> film);

    void delete(Optional<Film> film);

    Optional<Film> getById(long id);

    List<Film> getAllFilms();

    List<Film> getTopFilms(int count);

    void deleteLike(Optional<User> user, Optional<Film> film);

    void addLike(Optional<User> user, Optional<Film> film);


}
