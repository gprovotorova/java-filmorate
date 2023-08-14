package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.MpaDaoImpl;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Service("filmService")
public class FilmService {

    @Qualifier("filmDbStorage")
    private final FilmDbStorage filmStorage;

    @Qualifier("genreDaoImpl")
    private final GenreDaoImpl genreDao;

    @Qualifier("mpaDaoImpl")
    private final MpaDaoImpl mpaDao;

    @Autowired
    public FilmService(FilmDbStorage filmStorage, GenreDaoImpl genreDao, MpaDaoImpl mpaDao) {
        this.filmStorage = filmStorage;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
    }

    public Optional<Film> saveFilm(Film film) {
        Optional<Film> newfilm = filmStorage.save(Optional.of(film));
        return newfilm;
    }

    public Film updateFilm(Film film) {
        Optional<Film> newfilm = filmStorage.update(Optional.of(film));
        if (newfilm.get() == null) {
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
        }
        return newfilm.get();
    }

    public void deleteFilm(Film film) {
        filmStorage.delete(Optional.of(film));
    }

    public Film getById(long filmId) {
        Film film = filmStorage.getById(filmId).orElseThrow(() ->
                new NotFoundException("Фильм с id " + filmId + " не найден"));
        return film;
    }

    public void addLike(User user, Film film) {
        filmStorage.addLike(Optional.of(user), Optional.of(film));
    }

    public void deleteLike(User user, Film film) {
        filmStorage.deleteLike(Optional.of(user), Optional.of(film));
    }

    public List<Film> getTopFilms(int count) {
        List<Film> topFilms = filmStorage.getTopFilms(count);
        if (topFilms.isEmpty()) {
            new NotFoundException("Произошла ошибка во время поиска популярных фильмов");
        }
        return topFilms;
    }

    public List<Film> getAllFilms() {
        List<Film> films = filmStorage.getAllFilms();
        if (films.isEmpty()) {
            new NotFoundException("Произошла ошибка во время поиска фильмов");
        }
        return films;
    }

    public List<Genres> getGenres() {
        List<Genres> genres = genreDao.getAll();
        if (genres.isEmpty()) {
            new NotFoundException("Произошла ошибка во время вывода жанров");
        }
        return genres;
    }

    public Genres getGenreById(long id) {
        Genres genre = genreDao.getById(id);
        if (genre == null) {
            new NotFoundException("Произошла ошибка во время вывода жанра по id = " + id);
        }
        return genre;
    }

    public List<Mpa> getAllMpa() {
        List<Mpa> mpa = mpaDao.getAll();
        if (mpa.isEmpty()) {
            new NotFoundException("Произошла ошибка во время вывода рейтингов");
        }
        return mpa;
    }

    public Mpa getMpaById(long id) {
        Mpa mpa = mpaDao.getById(id);
        if (mpa == null) {
            new NotFoundException("Произошла ошибка во время вывода рейтинга по id = " + id);
        }
        return mpa;
    }
}
