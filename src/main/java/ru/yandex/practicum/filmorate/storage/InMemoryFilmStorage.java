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
    public Map<Long, Set<Long>> filmUsersLikes = new HashMap<>();

    @Override
    public Optional<Film> getById(long filmId) {
        Film film = films.get(filmId);
        return Optional.ofNullable(film);
    }

    @Override
    public Optional<Film> save(Optional<Film> film) {
        film.get().setId(++id);
        films.put(film.get().getId(), film.get());
        filmUsersLikes.put(film.get().getId(), getLikes(film));
        return film;
    }

    private Set<Long> getLikes(Optional<Film> film) {
        Set<Long> filmLikes = filmUsersLikes.computeIfAbsent(film.get().getId(), id -> new HashSet<>());
        return filmLikes;
    }

    @Override
    public Optional<Film> update(Optional<Film> film) {
        checkId(film);
        films.put(film.get().getId(), film.get());
        filmUsersLikes.replace(film.get().getId(), getLikes(film));
        return film;
    }

    @Override
    public void delete(Optional<Film> film) {
        films.remove(film.get().getId());
        filmUsersLikes.remove(film.get().getId(), getLikes(film));
    }

    @Override
    public void addLike(Optional<User> user, Optional<Film> film) {
        Set<Long> filmLikes = filmUsersLikes.computeIfAbsent(film.get().getId(), id -> new HashSet<>());
        filmLikes.add(user.get().getId());
        filmUsersLikes.replace(film.get().getId(), filmLikes);
    }

    @Override
    public void deleteLike(Optional<User> user, Optional<Film> film) {
        Set<Long> filmLikes = filmUsersLikes.computeIfAbsent(film.get().getId(), id -> new HashSet<>());
        filmLikes.remove(user.get().getId());
        filmUsersLikes.replace(film.get().getId(), filmLikes);
    }

    @Override
    public List<Film> getTopFilms(int count) {
        Map<Film, Long> filmLikes = new HashMap<>();
        for (long filmId : filmUsersLikes.keySet()) {
            filmLikes.put(getById(filmId).get(), Long.valueOf(getLikes(getById(filmId)).size()));
        }
        List<Film> sortedFilms = filmLikes.entrySet().stream()
                .sorted(Map.Entry.<Film, Long>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .limit(count)
                .collect(Collectors.toList());
        return sortedFilms;
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    public void checkId(Optional<Film> film) {
        for (Long id : films.keySet()) {
            if (id == film.get().getId()) {
                return;
            }
        }
        throw new NotFoundException("Такого фильма пока нет в нашей фильмотеке.");
    }
}
