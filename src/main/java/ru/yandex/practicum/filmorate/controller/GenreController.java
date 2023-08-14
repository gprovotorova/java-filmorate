package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/genres")
@Validated
public class GenreController {
    @Qualifier("filmService")
    private final FilmService filmService;

    @Autowired
    public GenreController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Genres> getGenres() {
        log.debug("+ getGenres: {}", filmService.getGenres().size());
        return filmService.getGenres();
    }

    @GetMapping("/{id}")
    public Genres getGenreById(@PathVariable("id") long id) {
        log.debug("+ getGenreById: {}", filmService.getGenreById(id));
        return filmService.getGenreById(id);
    }
}
