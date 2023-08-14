package ru.yandex.practicum.filmorate.dao;

import java.util.List;

import ru.yandex.practicum.filmorate.model.Genres;

public interface GenreDao {
    Genres getById(long id);

    List<Genres> getAll();
}
