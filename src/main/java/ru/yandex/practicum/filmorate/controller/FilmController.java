package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
@Validated
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    public void save(Film film) {
        log.debug("+ save: {}", film);
        films.put(film.getId(), film);
    }

    public void checkId(Film film) {
        for (Long id : films.keySet()) {
            if (id == film.getId()) {
                return;
            }
        }
        throw new ValidationException("Такого фильма пока нет в нашей фильмотеке.");
    }

    @GetMapping
    public List<Film> getFilms() {
        log.debug("getFilms {}", films);
        log.info("Текущее количество фильмов: {} + getFilms", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        log.debug("+ create: {}", film);
        film.generateId(films);
        save(film);
        log.debug("+ create: {}", film);
        return film;
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        log.debug("+ put: {}", film);
        checkId(film);
        save(film);
        log.debug("+ put: {}", film);
        return film;
    }
}
