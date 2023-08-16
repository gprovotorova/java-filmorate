package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Component("genreDaoImpl")
@RequiredArgsConstructor
public class GenreDaoImpl implements GenreDao {

    private static final String SELECT_BY_ID = "select genre_id, genre_name from genres where genre_id = :genre_id";
    private static final String SELECT_ALL = "select genre_id, genre_name from genres";
    private static final String INSERT_INTO_FILMS_GENRE = "insert into films_genre (film_id, genre_id) " +
            "values (:film_id, :genre_id)";
    private static final String DELETE_FILMS_GENRE = "delete from films_genre " +
            "where film_id = :film_id and genre_id = :genre_id";
    private static final String DELETE_GENRE = "delete from films_genre where film_id = :film_id";
    private static final String SELECT_GENRES_BY_IDS = "select fg.film_id, fg.genre_id, g.genre_name " +
            "from films_genre as fg " +
            "left join genres as g " +
            "on fg.genre_id = g.genre_id " +
            "where film_id = :film_id";

    private final Logger log = LoggerFactory.getLogger(GenreDaoImpl.class);
    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    public Genres getById(long id) {
        final List<Genres> genre = jdbcOperations.query(SELECT_BY_ID, Map.of("genre_id", id), new GenreRowMapper());
        if (genre.size() != 1 || genre.isEmpty()) {
            log.info("Жанр с идентификатором {} не найден.", id);
            throw new NotFoundException("Жанр с идентификатором " + id + " не найден.");
        }
        return genre.get(0);
    }

    @Override
    public List<Genres> getAll() {
        return jdbcOperations.query(SELECT_ALL, new GenreRowMapper());
    }

    public void setGenreDatabase(Film film, long id) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("film_id", film.getId());
        map.addValue("genre_id", id);
        jdbcOperations.update(INSERT_INTO_FILMS_GENRE, map, keyHolder);
    }

    public void deleteGenreDatabase(Film film, long idDb) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("film_id", film.getId());
        map.addValue("genre_id", idDb);
        jdbcOperations.update(DELETE_FILMS_GENRE, map);
    }

    public void loadFilmGenre(List<Film> films) {
        List<Long> ids = new ArrayList<>();
        for (Film film : films) {
            ids.add(film.getId());
        }
        for (Long id : ids) {
            final List<Genres> genres = jdbcOperations.query(SELECT_GENRES_BY_IDS, Map.of("film_id", id),
                    new GenreRowMapper());
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
        List<Genres> genresDatabase = jdbcOperations.query(SELECT_GENRES_BY_IDS, Map.of("film_id", film.getId()),
                new GenreRowMapper());
        for (Genres genreDB : genresDatabase) {
            idsFormDatabase.add(genreDB.getId());
        }
        if (ids.size() > idsFormDatabase.size()) {
            for (Long id : ids) {
                for (Long idDb : idsFormDatabase) {
                    if (id != idDb) {
                        setGenreDatabase(film, id);
                    }
                }
            }
        }
        if (ids.size() < idsFormDatabase.size()) {
            for (Long idDb : idsFormDatabase) {
                deleteGenreDatabase(film, idDb);
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
            setGenreDatabase(film, id);
        }
    }

    public void deleteGenre(Film film) {
        jdbcOperations.update(DELETE_GENRE, Map.of("film_id", film.getId()));
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
