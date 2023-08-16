package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Map;


@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private static final String INSERT_INTO = "insert into films (film_name, film_description, release_date, " +
            "duration, rate, rating_id) values (:film_name, :film_description, :release_date, " +
            ":duration, :rate, :rating_id)";
    private static final String UPDATE = "update films set film_name = :film_name, " +
            "film_description = :film_description, release_date = :release_date, duration = :duration, rate = :rate, " +
            "rating_id = :rating_id where film_id = :film_id";
    private static final String DELETE = "delete from films where film_id = :film_id";
    private static final String SELECT_BY_ID = "select f.film_id, f.film_name, f.film_description, f.release_date, " +
            "f.duration, f.rate, f.rating_id, fr.rating_name from films as f left join films_rating as fr " +
            "on f.rating_id = fr.rating_id where film_id = :film_id";
    private static final String ADD_LIKE = "update films set film_name = :film_name, " +
            "film_description = :film_description, release_date = :release_date, duration = :duration, rate = :rate, " +
            "rating_id = :rating_id where film_id = :film_id";
    private static final String DELETE_LIKE = "update films set film_name = :film_name, " +
            "film_description = :film_description, release_date = :release_date, duration = :duration, rate = :rate, " +
            "rating_id = :rating_id where film_id = :film_id";
    private static final String SELECT_TOP_FILMS = "select f.film_id, f.film_name, f.film_description, f.release_date, " +
            "f.duration, f.rate, f.rating_id, fg.rating_name " +
            "from films as f " +
            "left join films_rating as fg on f.rating_id = fg.rating_id " +
            "group by f.film_id " +
            "order by f.rate desc " +
            "limit :count";
    private static final String SELECT_ALL = "select f.film_id, f.film_name, f.film_description, f.release_date, " +
            "f.duration, f.rate, f.rating_id, fg.rating_name " +
            "from films as f " +
            "left join films_rating as fg " +
            "on f.rating_id = fg.rating_id";

    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);

    private final NamedParameterJdbcOperations jdbcOperations;
    @Qualifier("mpaDaoImpl")
    @Autowired
    private final MpaDaoImpl mpaDao;

    @Qualifier("genreDaoImpl")
    @Autowired
    private final GenreDaoImpl genreDao;

    @Override
    public Optional<Film> save(Optional<Film> film) {
        if (film.isPresent()) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            MapSqlParameterSource map = new MapSqlParameterSource();
            map.addValue("film_name", film.get().getName());
            map.addValue("film_description", film.get().getDescription());
            map.addValue("release_date", film.get().getReleaseDate());
            map.addValue("duration", film.get().getDuration());
            map.addValue("rate", film.get().getRate());
            map.addValue("rating_id", film.get().getMpa().getId());
            jdbcOperations.update(INSERT_INTO, map, keyHolder);
            film.get().setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
            if (film.get().getGenres() != null) {
                genreDao.setGenres(film.get());
            }
            mpaDao.setMpa(film);
            genreDao.loadFilmGenre(List.of(film.get()));
        } else {
            throw new NullPointerException("Передан пустой объект.");
        }
        return film;
    }

    @Override
    public Optional<Film> update(Optional<Film> film) {
        if (film.isPresent()) {
            MapSqlParameterSource map = new MapSqlParameterSource();
            map.addValue("film_id", film.get().getId());
            map.addValue("film_name", film.get().getName());
            map.addValue("film_description", film.get().getDescription());
            map.addValue("release_date", film.get().getReleaseDate());
            map.addValue("duration", film.get().getDuration());
            map.addValue("rate", film.get().getRate());
            map.addValue("rating_id", film.get().getMpa().getId());
            jdbcOperations.update(UPDATE, map);
            if (film.get().getGenres() == null) {
                film.get().setGenres(new HashSet<>());
            } else if (film.get().getGenres().isEmpty()) {
                genreDao.updateGenres(film.get());
            } else if (film.get().getGenres() != null) {
                genreDao.updateGenres(film.get());
            }
            mpaDao.setMpa(film);
        } else {
            throw new NullPointerException("Передан пустой объект.");
        }
        return film;
    }

    @Override
    public void delete(Optional<Film> film) {
        if (film.isPresent()) {
            Long filmId = film.get().getId();
            jdbcOperations.update(DELETE, Map.of("film_id", filmId));
            if (!film.get().getGenres().isEmpty()) {
                genreDao.deleteGenre(film.get());
            }
        } else {
            throw new NullPointerException("Передан пустой объект.");
        }
    }

    @Override
    public Optional<Film> getById(long filmId) {
        final List<Film> films = jdbcOperations.query(SELECT_BY_ID, Map.of("film_id", filmId), new FilmRowMapper());
        if (films.size() != 1 || films.isEmpty()) {
            log.info("Фильм с идентификатором {} не найден.", filmId);
            return Optional.empty();
        }
        for (Film film : films) {
            mpaDao.setMpa(Optional.of(film));
        }
        genreDao.loadFilmGenre(films);
        return Optional.of(films.get(0));
    }

    @Override
    public void addLike(Optional<User> user, Optional<Film> film) {
        if (film.isPresent()) {
            int rate = film.get().getRate() + 1;
            MapSqlParameterSource map = new MapSqlParameterSource();
            map.addValue("film_id", film.get().getId());
            map.addValue("film_name", film.get().getName());
            map.addValue("film_description", film.get().getDescription());
            map.addValue("release_date", film.get().getReleaseDate());
            map.addValue("duration", film.get().getDuration());
            map.addValue("rate", rate);
            map.addValue("rating_id", film.get().getMpa().getId());
            jdbcOperations.update(ADD_LIKE, map);
        } else {
            throw new NullPointerException("Переданы пустые объекты.");
        }
    }

    @Override
    public void deleteLike(Optional<User> user, Optional<Film> film) {
        if (film.isPresent()) {
            int rate = film.get().getRate() - 1;
            MapSqlParameterSource map = new MapSqlParameterSource();
            map.addValue("film_id", film.get().getId());
            map.addValue("film_name", film.get().getName());
            map.addValue("film_description", film.get().getDescription());
            map.addValue("release_date", film.get().getReleaseDate());
            map.addValue("duration", film.get().getDuration());
            map.addValue("rate", rate);
            map.addValue("rating_id", film.get().getMpa().getId());
            jdbcOperations.update(DELETE_LIKE, map);
        } else {
            throw new NullPointerException("Переданы пустые объекты.");
        }
    }

    @Override
    public List<Film> getTopFilms(int count) {
        List<Film> films = jdbcOperations.query(SELECT_TOP_FILMS, Map.of("count", count), new FilmRowMapper());
        for (Film film : films) {
            mpaDao.setMpa(Optional.of(film));
            genreDao.loadFilmGenre(List.of(film));
        }
        return films;
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = jdbcOperations.query(SELECT_ALL, new FilmRowMapper());
        for (Film film : films) {
            mpaDao.setMpa(Optional.of(film));
            genreDao.loadFilmGenre(List.of(film));
        }
        return films;
    }

    private static class FilmRowMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Film(rs.getLong("film_id"),
                    rs.getString("film_name"),
                    rs.getString("film_description"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getDouble("duration"),
                    rs.getInt("rate"),
                    new Mpa(rs.getLong("rating_id"),
                            rs.getString("films_rating.rating_name"))
            );
        }
    }
}

