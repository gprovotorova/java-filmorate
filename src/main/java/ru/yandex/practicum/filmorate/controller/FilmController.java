package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
@Validated
public class FilmController {

    private static final FilmRepository filmRepository = new FilmRepository();
    private static final Map<Long, Film> films = filmRepository.getFilmsFromRepository();

    @GetMapping
    public List<Film> getFilms() {
        log.info("Текущее количество фильмов: {} + getFilms", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        film.generateId(films);
        log.info("Добавление фильма '" + film.getName() + "': {} - create", film);
        filmRepository.save(film);
        log.info("Фильм '" + film.getName() + "' - создан: {} - create", film);
        return film;
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        filmRepository.checkId(film);
        log.info("Обновление данных фильма '" + film.getName() + "': {} - put", film);
        filmRepository.save(film);
        log.info("Данные фильма '" + film.getName() + "' - обновлены: {} - put", film);
        return film;
    }
}
