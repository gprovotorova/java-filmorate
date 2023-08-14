package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component("genreDaoImpl")
@RequiredArgsConstructor
public class GenreDaoImpl implements GenreDao {

    private final Logger log = LoggerFactory.getLogger(GenreDaoImpl.class);
    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    public Genres getById(long id) {
        final String sql = "select genre_id, genre_name from genres where genre_id = :genre_id";
        final List<Genres> genre = jdbcOperations.query(sql, Map.of("genre_id", id), new GenreRowMapper());
        if (genre.size() != 1 || genre.isEmpty()) {
            log.info("Жанр с идентификатором {} не найден.", id);
            throw new NotFoundException("Жанр с идентификатором " + id + " не найден.");
        }
        return genre.get(0);
    }

    @Override
    public List<Genres> getAll() {
        final String sql = "select genre_id, genre_name from genres";
        return jdbcOperations.query(sql, new GenreRowMapper());
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
