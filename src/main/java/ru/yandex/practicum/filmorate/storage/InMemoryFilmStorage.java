package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private long id = 0;
    public final Map<Long, Film> films = new HashMap<>();
    public Map<Film, Set<Optional<User>>> filmUsersLikes = new HashMap<>();

    public Optional<Film> getById(long filmId) {
        Film film = films.get(filmId);
        if (film == null) {
            throw new NotFoundException("Фильма с таким id не существует.");
        }
        return Optional.of(film);
    }

    public Optional<Film> save(Optional<Film> film) {
        film.get().setId(++id);
        log.debug("+ save: {}", film);
        films.put(film.get().getId(), film.get());
        return film;
    }

    public Optional<Film> update(Optional<Film> film) {
        log.debug("+ update: {}", film);
        checkId(film);
        films.put(film.get().getId(), film.get());
        return film;
    }

    public void delete(Optional<Film> film) {
        log.debug("+ delete: {}", film);
        films.remove(film.get().getId());
    }

    public void checkId(Optional<Film> film) {
        for (Long id : films.keySet()) {
            if (id == film.get().getId()) {
                return;
            }
        }
        throw new NotFoundException("Такого фильма пока нет в нашей фильмотеке.");
    }

    public void addLike(Optional<User> user, Optional<Film> film) {
        Set<Optional<User>> filmLikes = new HashSet<>();
        filmLikes.add(user);
        filmUsersLikes.put(film.get(), filmLikes);
    }

    public List<Film> getTopFilms(int count) {
        Map<Film, Long> filmLikes = new HashMap<>();
        for (Film film : filmUsersLikes.keySet()) {
            filmLikes.put(film, Long.valueOf(filmUsersLikes.get(film).size()));
        }
        Map<Film, Long> topTenFilms =
                filmLikes.entrySet().stream()
                        .sorted(Map.Entry.<Film, Long>comparingByValue().reversed())
                        .limit(count)
                        .collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue()));

        return new ArrayList<>(topTenFilms.keySet().stream().collect(Collectors.toList()));
    }

    @Override
    public void deleteLike(Optional<User> user, Optional<Film> film) {
        Set<Optional<User>> filmLikes = filmUsersLikes.get(film);
        filmLikes.remove(user);
        filmUsersLikes.put(film.get(), filmLikes);
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }
}
