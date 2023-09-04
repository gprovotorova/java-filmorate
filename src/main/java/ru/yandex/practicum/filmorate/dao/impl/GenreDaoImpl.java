package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;


@Component("genreDaoImpl")
@RequiredArgsConstructor
@Slf4j
public class GenreDaoImpl implements GenreDao {

    private static final String SELECT_BY_ID = "select genre_id, genre_name from genres where genre_id = :genre_id";
    private static final String SELECT_ALL = "select genre_id, genre_name from genres";
    private static final String INSERT_INTO_FILMS_GENRE = "insert into films_genre (film_id, genre_id) " +
            "values (?, ?)";
    private static final String DELETE_FILMS_GENRE = "delete from films_genre " +
            "where film_id = ? and genre_id = ?";
    private static final String DELETE_GENRE = "delete from films_genre where film_id = :film_id";
    private static final String DELETE_ALL = "delete from films_genre";
    private static final String SELECT_GENRES_BY_IDS = "select fg.film_id, fg.genre_id, g.genre_name " +
            "from films_genre as fg " +
            "left join genres as g " +
            "on fg.genre_id = g.genre_id " +
            "where film_id = :film_id";

    private final NamedParameterJdbcOperations jdbcOperations;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Genres> getById(long id) {
        final List<Genres> genre = jdbcOperations.query(SELECT_BY_ID, Map.of("genre_id", id), (rs, i) ->
                new Genres(rs.getLong("genre_id"), rs.getString("genre_name")));
        if (genre.size() != 1 || genre.isEmpty()) {
            log.debug("Жанр с идентификатором {} не найден.", id);
            throw new ObjectNotFoundException("Жанр с идентификатором " + id + " не найден.");
        }
        return Optional.of(genre.get(0));
    }

    @Override
    public List<Genres> getAll() {
        return jdbcOperations.query(SELECT_ALL, (rs, i) ->
                new Genres(rs.getLong("genre_id"), rs.getString("genre_name")));
    }

    public void loadFilmGenre(List<Film> films) {
        List<Long> ids = new ArrayList<>();
        for (Film film : films) {
            ids.add(film.getId());
        }
        for (Long id : ids) {
            final List<Genres> genres = jdbcOperations.query(SELECT_GENRES_BY_IDS, Map.of("film_id", id), (rs, i) ->
                    new Genres(rs.getLong("genre_id"), rs.getString("genre_name")));
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
                (rs, i) -> new Genres(rs.getLong("genre_id"), rs.getString("genre_name")));
        for (Genres genreDB : genresDatabase) {
            idsFormDatabase.add(genreDB.getId());
        }
        List<Long> mismatchedIdsAdd = new ArrayList<>();
        if (ids.size() > idsFormDatabase.size()) {
            for (Long id : ids) {
                for (Long idDb : idsFormDatabase) {
                    if (id != idDb) {
                        mismatchedIdsAdd.add(id);
                    }
                }
            }
            batchOperations(INSERT_INTO_FILMS_GENRE, film, mismatchedIdsAdd);
        }

        List<Long> mismatchedIdsDelete = new ArrayList<>();
        if (ids.size() < idsFormDatabase.size()) {
            for (Long idDb : idsFormDatabase) {
                if (ids.isEmpty()) {
                    mismatchedIdsDelete.add(idDb);
                } else {
                    for (Long id : ids) {
                        if (idDb != id) {
                            mismatchedIdsDelete.add(idDb);
                        }
                    }
                }
            }
            batchOperations(DELETE_FILMS_GENRE, film, mismatchedIdsDelete);
        }
        setGenres(film);
    }

    public void setGenres(Film film) {
        List<Long> ids = new ArrayList<>();
        Set<Genres> genres = film.getGenres();
        for (Genres genre : genres) {
            ids.add(genre.getId());
        }
        if (genres == null || genres.isEmpty()) {
            film.setGenres(new HashSet<>());
            return;
        }
        batchOperations(INSERT_INTO_FILMS_GENRE, film, ids);
    }

    public void deleteGenre(Film film) {
        jdbcOperations.update(DELETE_GENRE, Map.of("film_id", film.getId()));
    }

    public void batchOperations(String sql, Film film, List<Long> ids) {
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, film.getId());
                ps.setLong(2, ids.get(i));
            }

            @Override
            public int getBatchSize() {
                return ids.size();
            }
        });
    }

    public void deleteAll() {
        jdbcOperations.update(DELETE_ALL, Map.of());
    }
}
