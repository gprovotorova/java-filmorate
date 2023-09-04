package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component("mpaDaoImpl")
@RequiredArgsConstructor
@Slf4j
public class MpaDaoImpl implements MpaDao {

    private static final String SELECT_BY_ID = "select rating_id, rating_name from films_rating " +
            "where rating_id = :rating_id";
    private static final String SELECT_ALL = "select rating_id, rating_name from films_rating";

    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    public Optional<Mpa> getById(long id) {
        final List<Mpa> rating = jdbcOperations.query(SELECT_BY_ID, Map.of("rating_id", id), (rs, i) ->
                new Mpa(rs.getLong("rating_id"), rs.getString("rating_name")));
        if (rating.size() != 1 || rating.isEmpty()) {
            log.debug("Рейтинг с идентификатором {} не найден.", id);
            throw new ObjectNotFoundException("Рейтинг с идентификатором " + id + " не найден.");
        }
        return Optional.of(rating.get(0));
    }

    @Override
    public List<Mpa> getAll() {
        return jdbcOperations.query(SELECT_ALL, (rs, i) ->
                new Mpa(rs.getLong("rating_id"), rs.getString("rating_name")));
    }

    public void loadMpa(Film film) {
        long mpaId = film.getMpa().getId();
        Mpa mpa = getById(mpaId).get();
        film.setMpa(mpa);
    }
}
