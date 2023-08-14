package ru.yandex.practicum.filmorate.model;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.impl.GenreDaoImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDaoTest {
    @Autowired
    public GenreDaoImpl genreDao;

    @DisplayName("проверка получения одного жанра")
    @Test
    public void getById() {
        Genres genreDB = genreDao.getById(1);
        Genres genreCheck = new Genres(1, "Комедия");
        assertEquals(genreCheck, genreDB);
    }

    @DisplayName("проверка получения всех жанров")
    @Test
    public void getAll() {
        List<Genres> genresDB = genreDao.getAll();
        List<Genres> genresCheck = List.of(new Genres(1, "Комедия"), new Genres(2, "Драма"),
                new Genres(3, "Мультфильм"), new Genres(4, "Триллер"),
                new Genres(5, "Документальный"), new Genres(6, "Боевик"));
        assertEquals(genresCheck, genresDB);
    }
}
