package ru.yandex.practicum.filmorate.dao;

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
import ru.yandex.practicum.filmorate.dao.impl.MpaDaoImpl;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);

    private final NamedParameterJdbcOperations jdbcOperations;
    @Qualifier("mpaDaoImpl")
    @Autowired
    private final MpaDaoImpl mpaDao;

    @Override
    public Optional<Film> save(Optional<Film> film) {
        String sqlQuery = "insert into films (film_name, film_description, release_date, duration, rate, rating_id) " +
                "values (:film_name, :film_description, :release_date, :duration, :rate, :rating_id)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("film_name", film.get().getName());
        map.addValue("film_description", film.get().getDescription());
        map.addValue("release_date", film.get().getReleaseDate());
        map.addValue("duration", film.get().getDuration());
        map.addValue("rate", film.get().getRate());
        map.addValue("rating_id", film.get().getMpa().getId());
        jdbcOperations.update(sqlQuery, map, keyHolder);
        film.get().setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        if (film.get().getGenres() != null) {
            setGenres(film.get());
        }
        setMpa(film);
        loadFilmGenre(List.of(film.get()));
        return film;
    }

    @Override
    public Optional<Film> update(Optional<Film> film) {
        String sqlQuery = "update films set film_name = :film_name, film_description = :film_description, " +
                "release_date = :release_date, duration = :duration, rate = :rate, rating_id = :rating_id " +
                "where film_id = :film_id";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("film_id", film.get().getId());
        map.addValue("film_name", film.get().getName());
        map.addValue("film_description", film.get().getDescription());
        map.addValue("release_date", film.get().getReleaseDate());
        map.addValue("duration", film.get().getDuration());
        map.addValue("rate", film.get().getRate());
        map.addValue("rating_id", film.get().getMpa().getId());
        jdbcOperations.update(sqlQuery, map);
        if (film.get().getGenres() == null) {
            film.get().setGenres(new HashSet<>());
        } else if (film.get().getGenres().isEmpty()) {
            updateGenres(film.get());
        } else if (film.get().getGenres() != null) {
            updateGenres(film.get());
        }
        setMpa(film);
        return film;
    }

    @Override
    public void delete(Optional<Film> film) {
        String sqlQuery = "delete from films where film_id = :film_id";
        Long filmId = film.get().getId();
        jdbcOperations.update(sqlQuery, Map.of("film_id", filmId));
        if (!film.get().getGenres().isEmpty()) {
            String sql = "delete from films_genre where film_id = :film_id";
            jdbcOperations.update(sql, Map.of("film_id", film.get().getId()));
        }
    }

    @Override
    public Optional<Film> getById(long filmId) {
        final String sql = "select f.film_id, f.film_name, f.film_description, f.release_date, f.duration, " +
                "f.rate, f.rating_id, fr.rating_name " +
                "from films as f  " +
                "left join films_rating as fr  " +
                "on f.rating_id = fr.rating_id " +
                "where film_id = :film_id";
        final List<Film> films = jdbcOperations.query(sql, Map.of("film_id", filmId), new FilmRowMapper());
        if (films.size() != 1 || films.isEmpty()) {
            log.info("Фильм с идентификатором {} не найден.", filmId);
            return Optional.empty();
        }
        for (Film film : films) {
            setMpa(Optional.of(film));
        }
        loadFilmGenre(films);
        return Optional.of(films.get(0));
    }

    @Override
    public void addLike(Optional<User> user, Optional<Film> film) {
        String sqlIncreaseRate = "update films set film_name = :film_name, film_description = :film_description, " +
                "release_date = :release_date, duration = :duration, rate = :rate, rating_id = :rating_id " +
                "where film_id = :film_id";

        int rate = film.get().getRate() + 1;

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("film_id", film.get().getId());
        map.addValue("film_name", film.get().getName());
        map.addValue("film_description", film.get().getDescription());
        map.addValue("release_date", film.get().getReleaseDate());
        map.addValue("duration", film.get().getDuration());
        map.addValue("rate", rate);
        map.addValue("rating_id", film.get().getMpa().getId());
        jdbcOperations.update(sqlIncreaseRate, map);
    }

    @Override
    public void deleteLike(Optional<User> user, Optional<Film> film) {
        String sqldecreaseRate = "update films set film_name = :film_name, film_description = :film_description, " +
                "release_date = :release_date, duration = :duration, rate = :rate, rating_id = :rating_id " +
                "where film_id = :film_id";

        int rate = film.get().getRate() - 1;

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("film_id", film.get().getId());
        map.addValue("film_name", film.get().getName());
        map.addValue("film_description", film.get().getDescription());
        map.addValue("release_date", film.get().getReleaseDate());
        map.addValue("duration", film.get().getDuration());
        map.addValue("rate", rate);
        map.addValue("rating_id", film.get().getMpa().getId());
        jdbcOperations.update(sqldecreaseRate, map);
    }

    @Override
    public List<Film> getTopFilms(int count) {
        String sqlQuery = "select f.film_id, f.film_name, f.film_description, f.release_date, f.duration, " +
                "f.rate, f.rating_id, fg.rating_name " +
                "from films as f " +
                "left join films_rating as fg on f.rating_id = fg.rating_id " +
                "group by f.film_id " +
                "order by f.rate desc " +
                "limit :count";
        List<Film> films = jdbcOperations.query(sqlQuery, Map.of("count", count), new FilmRowMapper());
        for (Film film : films) {
            setMpa(Optional.of(film));
            loadFilmGenre(List.of(film));
        }
        return films;
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "select f.film_id, f.film_name, f.film_description, f.release_date, f.duration, " +
                "f.rate, f.rating_id, fg.rating_name " +
                "from films as f " +
                "left join films_rating as fg " +
                "on f.rating_id = fg.rating_id";
        List<Film> films = jdbcOperations.query(sqlQuery, new FilmRowMapper());
        for (Film film : films) {
            setMpa(Optional.of(film));
            loadFilmGenre(List.of(film));
        }
        return films;
    }

    public void loadFilmGenre(List<Film> films) {
        List<Long> ids = new ArrayList<>();
        for (Film film : films) {
            ids.add(film.getId());
        }
        for (Long id : ids) {
            String sql = "select fg.film_id, fg.genre_id, g.genre_name " +
                    "from films_genre as fg " +
                    "left join genres as g " +
                    "on fg.genre_id = g.genre_id " +
                    "where film_id = :film_id";
            final List<Genres> genres = jdbcOperations.query(sql, Map.of("film_id", id), new GenreRowMapper());
            for (Film film : films) {
                film.setGenres(new HashSet<>(genres));
            }
        }
    }

    public void updateGenres(Film film) {
        List<Long> ids = new ArrayList<>();
        Set<Genres> genres = film.getGenres();
        for (Genres genre : genres) {
            ids.add(genre.getId());
        }
        List<Long> idsFormDatabase = new ArrayList<>();
        String sql = "select fg.film_id, fg.genre_id, g.genre_name " +
                "from films_genre as fg " +
                "left join genres as g " +
                "on fg.genre_id = g.genre_id " +
                "where film_id = :film_id";
        final List<Genres> genresDatabase = jdbcOperations.query(sql, Map.of("film_id", film.getId()), new GenreRowMapper());
        for (Genres genreDB : genresDatabase) {
            idsFormDatabase.add(genreDB.getId());
        }
        if (ids.size() > idsFormDatabase.size()) {
            for (Long id : ids) {
                for (Long idDb : idsFormDatabase) {
                    if (id != idDb) {
                        String sqlQuery = "insert into films_genre (film_id, genre_id) values (:film_id, :genre_id)";
                        MapSqlParameterSource map = new MapSqlParameterSource();
                        map.addValue("film_id", film.getId());
                        map.addValue("genre_id", id);
                        jdbcOperations.update(sqlQuery, map);
                    }
                }
            }
        }
        if (ids.size() < idsFormDatabase.size()) {
            for (Long idDb : idsFormDatabase) {
                String sqlQuery = "delete from films_genre where film_id = :film_id and genre_id = :genre_id";
                MapSqlParameterSource map = new MapSqlParameterSource();
                map.addValue("film_id", film.getId());
                map.addValue("genre_id", idDb);
                jdbcOperations.update(sqlQuery, map);
            }
        }
        setGenres(film);
    }

    public void setGenres(Film film) {
        List<Long> ids = new ArrayList<>();
        Set<Genres> genres = film.getGenres();
        for (Genres genre : genres) {
            ids.add(genre.getId());
        }
        if (genres == null) {
            film.setGenres(new HashSet<>());
            return;
        }
        for (Long id : ids) {
            String sql = "insert into films_genre (film_id, genre_id) values (:film_id, :genre_id)";

            KeyHolder keyHolder = new GeneratedKeyHolder();
            MapSqlParameterSource map = new MapSqlParameterSource();
            map.addValue("film_id", film.getId());
            map.addValue("genre_id", id);
            jdbcOperations.update(sql, map, keyHolder);
        }
    }

    private void setMpa(Optional<Film> film) {
        long mpaId = film.get().getMpa().getId();
        Mpa mpa = mpaDao.getById(mpaId);
        film.get().setMpa(mpa);
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

    private static class GenreRowMapper implements RowMapper<Genres> {
        @Override
        public Genres mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Genres(rs.getLong("genre_id"),
                    rs.getString("genre_name")
            );
        }
    }
}

