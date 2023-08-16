package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.*;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilmDbStorageTest {
    @Autowired
    public FilmDbStorage filmStorage;
    @Autowired
    public UserDbStorage userStorage;

    @DisplayName("проверка создания и возвращения фильма")
    @Test
    @Order(1)
    public void createAndGetFilmById() {
        Optional<Film> film = Optional.of(new Film("Унесённые призраками", "Шедевр Хаяо Миядзаки",
                LocalDate.of(2001, 7, 20), 125, 5, new Mpa(2, "PG"),
                new HashSet<>(Set.of(new Genres(3, "Мультфильм")))));
        film = filmStorage.save(film);
        assertEquals(film, filmStorage.getById(1));
    }

    @DisplayName("проверка обновления и возвращения фильма")
    @Test
    @Order(2)
    public void updateAndGetFilmById() {
        Optional<Film> film = Optional.of(new Film("Леон - супер фильм!",
                "Культовый триллер с Жаном Рено и Натали Портман.",
                LocalDate.of(1994, 9, 14), 133, 0, new Mpa(4, "R"),
                new HashSet<>(Set.of(new Genres(4, "Триллер"), new Genres(6, "Боевик")))));
        filmStorage.save(film);

        film.get().setName("Леон");
        filmStorage.update(film);
        assertEquals(film, filmStorage.getById(2));
    }

    @DisplayName("проверка удаления фильма")
    @Test
    @Order(3)
    public void deleteFilmById() {
        Optional<Film> filmDeleteCheck = Optional.of(new Film("name", "description",
                LocalDate.of(1994, 9, 14), 133, 0, new Mpa(4, "R")));
        filmStorage.save(filmDeleteCheck);
        filmStorage.delete(filmDeleteCheck);
        assertTrue(filmStorage.getById(3).isEmpty());
    }

    @DisplayName("проверка возвращения фильмов")
    @Test
    @Order(4)
    public void getAllFilms() {
        Optional<Film> firstFilm = filmStorage.getById(1);
        Optional<Film> secondFilm = Optional.of(new Film("Тутси",
                "Комедия с фееричным перевоплощением Дастина Хоффмана",
                LocalDate.of(1982, 12, 1), 125, 3, new Mpa(2, "PG"),
                new HashSet<>(Set.of(new Genres(1, "Комедия")))));
        secondFilm = filmStorage.save(secondFilm);
        Optional<Film> thirdFilm = filmStorage.getById(2);
        List<Film> films = List.of(firstFilm.get(), thirdFilm.get(), secondFilm.get());
        assertNotNull(filmStorage.getAllFilms());
        assertEquals(films, filmStorage.getAllFilms());
    }

    @DisplayName("проверка возвращения популярных фильмов")
    @Test
    @Order(5)
    public void getTopFilms() {
        Optional<Film> firstFilm = filmStorage.getById(1);
        Optional<Film> secondFilm = filmStorage.getById(2);
        Optional<Film> thirdFilm = filmStorage.getById(4);

        List<Film> filmOne = List.of(firstFilm.get());
        List<Film> films = List.of(firstFilm.get(), thirdFilm.get(), secondFilm.get());
        assertEquals(filmOne, filmStorage.getTopFilms(1));
        assertEquals(films, filmStorage.getTopFilms(10));
    }

    @DisplayName("проверка добавления и удаления лайка")
    @Test
    @Order(6)
    public void addAndDeleteLike() {
        Optional<Film> film = filmStorage.getById(1);

        Optional<User> user = userStorage.getById(1);

        filmStorage.addLike(user, film);
        assertEquals(6, filmStorage.getById(1).get().getRate());

        filmStorage.deleteLike(user, film);
        assertEquals(4, filmStorage.getById(1).get().getRate());
    }
}
