package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

public class FilmRepository {
    private final Map<Long, Film> films = new HashMap<>();

    public void save(Film film) {
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

    public Map<Long, Film> getFilmsFromRepository() {
        return films;
    }
}
