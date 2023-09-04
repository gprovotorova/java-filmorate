package ru.yandex.practicum.filmorate.dao;

import java.util.List;
import java.util.Optional;

import ru.yandex.practicum.filmorate.model.Genres;

public interface GenreDao {
    Optional<Genres> getById(long id);

    List<Genres> getAll();
}
