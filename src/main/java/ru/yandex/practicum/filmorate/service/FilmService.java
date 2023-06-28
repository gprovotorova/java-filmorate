package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.List;
import java.util.Optional;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addLike(User user, Film film) {
        filmStorage.addLike(Optional.of(user), Optional.of(film));
    }

    public void deleteLike(User user, Film film) {
        filmStorage.deleteLike(Optional.of(user), Optional.of(film));
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getTopFilms(count);
    }

    public Film getById(long filmId) {
        final Film film = filmStorage.getById(filmId).get();
        if (film == null) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
        return film;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film updateFilm(Film film) {
        filmStorage.update(Optional.of(film));
        return film;
    }

    public Film saveFilm(Film film) {
        filmStorage.save(Optional.of(film));
        return film;
    }

    public void deleteFilm(Film film) {
        filmStorage.delete(Optional.of(film));
    }
}
