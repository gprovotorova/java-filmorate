package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.MpaDaoImpl;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Service("filmService")
public class FilmService {

    private final FilmDbStorage filmStorage;
    private final GenreDaoImpl genreDao;
    private final MpaDaoImpl mpaDao;

    @Autowired
    public FilmService(FilmDbStorage filmStorage, GenreDaoImpl genreDao, MpaDaoImpl mpaDao) {
        this.filmStorage = filmStorage;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
    }

    public Film saveFilm(Film film) {
        filmStorage.save(film);
        return filmStorage.getById(film.getId()).orElseThrow(() ->
                new ObjectNotFoundException("Возвращается пустой объект после сохранения фильма."));
    }

    public Film updateFilm(Film film) {
        filmStorage.update(film);
        return filmStorage.getById(film.getId()).orElseThrow(() ->
                new ObjectNotFoundException("Фильм с id = " + film.getId() + " не найден"));
    }

    public void deleteFilm(Film film) {
        filmStorage.delete(film);
    }

    public Film getById(long filmId) {
        return filmStorage.getById(filmId).orElseThrow(() ->
                new ObjectNotFoundException("Фильм с id " + filmId + " не найден"));
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
            throw new ObjectNotFoundException("Произошла ошибка во время поиска популярных фильмов");
        }
        return topFilms;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public List<Genres> getGenres() {
        final List<Genres> genres = genreDao.getAll();
        if (genres.isEmpty()) {
            throw new ObjectNotFoundException("Произошла ошибка во время вывода жанров");
        }
        return genres;
    }

    public Genres getGenreById(long id) {
        final Genres genre = genreDao.getById(id).get();
        if (genre == null) {
            throw new ObjectNotFoundException("Произошла ошибка во время вывода жанра по id = " + id);
        }
        return genre;
    }

    public List<Mpa> getAllMpa() {
        final List<Mpa> mpa = mpaDao.getAll();
        if (mpa.isEmpty()) {
            throw new ObjectNotFoundException("Произошла ошибка во время вывода рейтингов");
        }
        return mpa;
    }

    public Mpa getMpaById(long id) {
        final Mpa mpa = mpaDao.getById(id).get();
        if (mpa == null) {
            throw new ObjectNotFoundException("Произошла ошибка во время вывода рейтинга по id = " + id);
        }
        return mpa;
    }
}
