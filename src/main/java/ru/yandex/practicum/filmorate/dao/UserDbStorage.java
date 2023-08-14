package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);

    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    public Optional<User> save(Optional<User> user) {
        String sqlQuery = "insert into users (email, login, user_name, birthday) " +
                "values (:email, :login, :user_name, :birthday)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("email", user.get().getEmail());
        map.addValue("login", user.get().getLogin());
        map.addValue("user_name", user.get().getName());
        map.addValue("birthday", user.get().getBirthday());
        jdbcOperations.update(sqlQuery, map, keyHolder);
        user.get().setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    @Override
    public Optional<User> update(Optional<User> user) {
        String sqlQuery = ("update users set email = :email, login = :login, user_name = :user_name, birthday = :birthday " +
                "where user_id = :user_id");

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("user_id", user.get().getId());
        map.addValue("email", user.get().getEmail());
        map.addValue("login", user.get().getLogin());
        map.addValue("user_name", user.get().getName());
        map.addValue("birthday", user.get().getBirthday());
        jdbcOperations.update(sqlQuery, map);
        return user;
    }

    @Override
    public void delete(Optional<User> user) {
        String sqlQuery = "delete from users where user_id = :user_id";
        jdbcOperations.update(sqlQuery, Map.of("user_id", user.get().getId()));
    }

    @Override
    public Optional<User> getById(long id) {
        final String sql = "select user_id, email, login, user_name, birthday from users where user_id = :user_id";
        final List<User> users = jdbcOperations.query(sql, Map.of("user_id", id), new UserRowMapper());
        if (users.size() != 1 || users.isEmpty()) {
            log.info("Пользователь с идентификатором {} не найден.", id);
            return Optional.empty();
        }
        return Optional.of(users.get(0));
    }

    @Override
    public List<User> getAllUsers() {
        final String sqlQuery = "select user_id, email, login, user_name, birthday from users";
        return jdbcOperations.query(sqlQuery, new UserRowMapper());
    }

    @Override
    public void addFriend(Optional<User> user, Optional<User> friend) {
        final String sqlQuery = "insert into friends (user_id, friend_id) " +
                "values (:user_id, :friend_id)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("user_id", user.get().getId());
        map.addValue("friend_id", friend.get().getId());
        jdbcOperations.update(sqlQuery, map, keyHolder);
    }

    @Override
    public void deleteFriend(Optional<User> user, Optional<User> friend) {
        final String sqlQuery = "delete from friends where user_id = :user_id and friend_id = :friend_id";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("user_id", user.get().getId());
        map.addValue("friend_id", friend.get().getId());
        jdbcOperations.update(sqlQuery, map);
    }

    @Override
    public List<User> getAllCommonFriends(Optional<User> user, Optional<User> friend) {
        final String sqlQuery = "select u.user_id, u.email, u.login, u.user_name, u.birthday " +
                "from users as u " +
                "inner join friends as f1 " +
                "on u.user_id = f1.friend_id " +
                "and f1.user_id = :user_id " +
                "inner join friends as f2 " +
                "on u.user_id = f2.friend_id " +
                "and f2.user_id = :friend_id";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("user_id", user.get().getId());
        map.addValue("friend_id", friend.get().getId());
        return jdbcOperations.query(sqlQuery, map, new UserRowMapper());
    }

    @Override
    public List<User> getFriends(Optional<User> user) {
        final String sqlQuery = "select u.user_id, u.email, u.login, u.user_name, u.birthday " +
                "from users as u " +
                "inner join friends as f " +
                "on u.user_id = f.friend_id " +
                "and f.user_id = :user_id";
        List<User> friends = jdbcOperations.query(sqlQuery, Map.of("user_id", user.get().getId()), new UserRowMapper());
        return friends;
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(rs.getLong("user_id"),
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("user_name"),
                    rs.getDate("birthday").toLocalDate()
            );
        }
    }
}


