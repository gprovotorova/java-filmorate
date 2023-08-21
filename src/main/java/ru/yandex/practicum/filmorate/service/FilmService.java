package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.MpaDaoImpl;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;


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

    public Film saveFilm(Film film) {
        filmStorage.save(film);
        Optional<Film> savedFilm = filmStorage.getById(film.getId());
        if (savedFilm == null) {
            throw new NotFoundException("Возвращается пустой объект после сохранения фильма.");
        }
        return savedFilm.get();
    }

    public Film updateFilm(Film film) {
        filmStorage.update(film);
        final Optional<Film> updatedFilm = filmStorage.getById(film.getId());
        if (updatedFilm == null) {
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
        }
        return updatedFilm.get();
    }

    public void deleteFilm(Film film) {
        filmStorage.delete(film);
    }

    public Film getById(long filmId) {
        return filmStorage.getById(filmId).orElseThrow(() ->
                new NotFoundException("Фильм с id " + filmId + " не найден"));
    }

    public void addLike(User user, Film film) {
        filmStorage.addLike(user, film);
    }

    public void deleteLike(User user, Film film) {
        filmStorage.deleteLike(user, film);
    }

    public List<Film> getTopFilms(int count) {
        final List<Film> topFilms = filmStorage.getTopFilms(count);
        if (topFilms.isEmpty()) {
            throw new NotFoundException("Произошла ошибка во время поиска популярных фильмов");
        }
        return topFilms;
    }

    public List<Film> getAllFilms() {
        final List<Film> films = filmStorage.getAllFilms();
        return films;
    }

    public List<Genres> getGenres() {
        final List<Genres> genres = genreDao.getAll();
        if (genres.isEmpty()) {
            throw new NotFoundException("Произошла ошибка во время вывода жанров");
        }
        return genres;
    }

    public Genres getGenreById(long id) {
        final Genres genre = genreDao.getById(id);
        if (genre == null) {
            throw new NotFoundException("Произошла ошибка во время вывода жанра по id = " + id);
        }
        return genre;
    }

    public List<Mpa> getAllMpa() {
        final List<Mpa> mpa = mpaDao.getAll();
        if (mpa.isEmpty()) {
            throw new NotFoundException("Произошла ошибка во время вывода рейтингов");
        }
        return mpa;
    }

    public Mpa getMpaById(long id) {
        final Mpa mpa = mpaDao.getById(id);
        if (mpa == null) {
            throw new NotFoundException("Произошла ошибка во время вывода рейтинга по id = " + id);
        }
        return mpa;
    }
}
