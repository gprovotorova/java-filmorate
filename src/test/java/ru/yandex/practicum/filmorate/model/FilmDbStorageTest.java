package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilmDbStorageTest extends AbstractTest {

    @Autowired
    public FilmDbStorage filmStorage;
    @Autowired
    public UserDbStorage userStorage;
    @Autowired
    public GenreDaoImpl genreDao;

    @BeforeEach
    public void beforeEach() {
        Film firstFilm = new Film("Унесённые призраками", "Шедевр Хаяо Миядзаки",
                LocalDate.of(2001, 7, 20), 125, 5, new Mpa(2, "PG"),
                new HashSet<>(Set.of(new Genres(3, "Мультфильм"))));
        filmStorage.save(firstFilm);
        Film secondFilm = new Film("Леон",
                "Культовый триллер с Жаном Рено и Натали Портман.",
                LocalDate.of(1994, 9, 14), 133, 3, new Mpa(4, "R"),
                new HashSet<>(Set.of(new Genres(3, "Мультфильм"))));
        filmStorage.save(secondFilm);
        Film thirdFilm = new Film("Тутси",
                "Комедия с фееричным перевоплощением Дастина Хоффмана",
                LocalDate.of(1982, 12, 1), 125, 0, new Mpa(2, "PG"),
                new HashSet<>(Set.of(new Genres(1, "Комедия"))));
        filmStorage.save(thirdFilm);
    }

    @AfterEach
    public void afterEach() {
        genreDao.deleteAll();
        filmStorage.deleteAll();
    }

    @DisplayName("проверка создания и возвращения фильма")
    @Test
    @Order(1)
    public void createAndGetFilmById() {
        Film film = new Film("Унесённые призраками", "Шедевр Хаяо Миядзаки",
                LocalDate.of(2001, 7, 20), 125, 5, new Mpa(2, "PG"),
                new HashSet<>(Set.of(new Genres(3, "Мультфильм"))));
        filmStorage.save(film);
        assertEquals(film, filmStorage.getById(4).get());
    }

    @DisplayName("проверка обновления и возвращения фильма")
    @Test
    @Order(2)
    public void updateAndGetFilmById() {
        Film film = filmStorage.getById(6).get();

        film.setName("Леон - супер фильм!");
        filmStorage.update(film);
        assertEquals(film, filmStorage.getById(6).get());
    }

    @DisplayName("проверка удаления фильма")
    @Test
    @Order(3)
    public void deleteFilmById() {
        Film filmDeleteCheck = new Film("name", "description",
                LocalDate.of(1994, 9, 14), 133d, 0, new Mpa(1, "G"));
        filmStorage.save(filmDeleteCheck);
        filmStorage.delete(filmDeleteCheck);
        assertTrue(filmStorage.getById(11).isEmpty());
    }

    @DisplayName("проверка возвращения фильмов")
    @Test
    @Order(4)
    public void getAllFilms() {
        Film firstFilm = filmStorage.getById(12).get();
        Film secondFilm = filmStorage.getById(13).get();
        Film thirdFilm = filmStorage.getById(14).get();

        List<Film> films = List.of(firstFilm, secondFilm, thirdFilm);
        assertNotNull(filmStorage.getAllFilms());
        assertEquals(films, filmStorage.getAllFilms());
    }

    @DisplayName("проверка возвращения популярных фильмов")
    @Test
    @Order(5)
    public void getTopFilms() {
        Film firstFilm = filmStorage.getById(15).get();
        Film secondFilm = filmStorage.getById(16).get();
        Film thirdFilm = filmStorage.getById(17).get();

        List<Film> filmOne = List.of(firstFilm);
        List<Film> films = List.of(firstFilm, secondFilm, thirdFilm);
        assertEquals(filmOne, filmStorage.getTopFilms(1));
        assertEquals(films, filmStorage.getTopFilms(10));
    }

    @DisplayName("проверка добавления и удаления лайка")
    @Test
    @Order(6)
    public void addAndDeleteLike() {
        Film film = filmStorage.getById(18).get();
        User user = new User("malina@mail.ru", "malina", "Алина Михайлова",
                LocalDate.of(2004, 2, 1));

        filmStorage.addLike(user, film);
        assertEquals(6, filmStorage.getById(18).get().getRate());

        filmStorage.deleteLike(user, film);
        assertEquals(4, filmStorage.getById(18).get().getRate());
    }
}
