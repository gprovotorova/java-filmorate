package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
@Validated
public class FilmController {

    private final FilmService filmService;
    private final UserService userService;

    @Autowired
    public FilmController(FilmService filmService, UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.debug("+ getFilms {}", filmService.getAllFilms().size());
        return new ArrayList<>(filmService.getAllFilms().size());
    }

    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable("filmId") int filmId) {
        log.debug("+ getFilmById: {}", filmService.getById(filmId));
        return filmService.getById(filmId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        log.debug("+ create: {}", film);
        Film saved = filmService.saveFilm(film);
        log.debug("+ create: {}", film);
        return saved;
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        log.debug("+ put: {}", film);
        Film saved = filmService.updateFilm(film);
        log.debug("+ put: {}", film);
        return saved;
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

    @GetMapping(value = {"/films/popular?count={count}", "/films/popular"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Film> getTopFilms(@RequestParam(value = "count", defaultValue = "10", required = false) String count) {
        List<Film> topFilms;
        if (count != null) {
            log.debug("+ getTopFilms: {}", count);
            topFilms = filmService.getTopFilms(Integer.parseInt(count));
            log.debug("+ getTopFilms: {}", count);
        } else {
            log.debug("+ getTopFilms: {}", getFilms().size());
            topFilms = filmService.getTopFilms(getFilms().size());
            log.debug("+ getTopFilms: {}", getFilms().size());
        }
        return topFilms;
    }
}
