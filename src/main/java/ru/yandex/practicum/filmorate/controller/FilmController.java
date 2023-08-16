package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;
import java.util.ArrayList;

@RestController
@Slf4j
@RequestMapping("/films")
@Validated
public class FilmController {

    @Qualifier("filmService")
    private final FilmService filmService;
    @Qualifier("userService")
    private final UserService userService;

    @Autowired
    public FilmController(FilmService filmService, UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.debug("+ getFilms {}", filmService.getAllFilms().size());
        return new ArrayList<>(filmService.getAllFilms());
    }

    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable("filmId") int filmId) {
        log.debug("+ getFilmById: {}", filmService.getById(filmId));
        return filmService.getById(filmId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@RequestBody @Valid Film film) {
        log.debug("+ create: {}", film);
        Film savedFilm = filmService.saveFilm(film);
        log.debug("+ create: {}", savedFilm);
        return savedFilm;
    }

    @PutMapping
    public Film put(@RequestBody @Valid Film film) {
        log.debug("+ put: {}", film);
        filmService.getById(film.getId());
        Film savedFilm = filmService.updateFilm(film);
        log.debug("+ put: {}", savedFilm);
        return savedFilm;
    }

    @DeleteMapping("/{filmId}")
    public void delete(@PathVariable("filmId") int filmId) {
        Film film = filmService.getById(filmId);
        log.debug("+ delete: {}", film);
        filmService.deleteFilm(film);
        log.debug("+ delete: {}", film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") int filmId, @PathVariable("userId") int userId) {
        Film film = filmService.getById(filmId);
        User user = userService.getById(userId);
        log.debug("+ addLike: {}", film);
        filmService.addLike(user, film);
        log.debug("+ addLike: {}", film);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") int filmId, @PathVariable("userId") int userId) {
        Film film = filmService.getById(filmId);
        User user = userService.getById(userId);
        log.debug("+ deleteLike: {}", film);
        filmService.deleteLike(user, film);
        log.debug("+ deleteLike: {}", film);
    }

    @GetMapping(value = {"/popular?count={count}", "/popular"})
    public List<Film> getTopFilms(@RequestParam(value = "count", required = false, defaultValue = "10") int count) {
        log.debug("+ getTopFilms: {}", getFilms().size());
        List<Film> topFilms = filmService.getTopFilms(count);
        log.debug("+ getTopFilms: {}", getFilms().size());
        return topFilms;
    }
}
