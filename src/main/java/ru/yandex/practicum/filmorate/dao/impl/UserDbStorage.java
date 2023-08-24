package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


@Component("userDbStorage")
@RequiredArgsConstructor
@Slf4j
public class UserDbStorage implements UserStorage {
    private static final String INSERT_USER = "insert into users (email, login, user_name, birthday) " +
            "values (:email, :login, :user_name, :birthday)";
    private static final String UPDATE_USER = "update users set email = :email, login = :login, " +
            "user_name = :user_name, birthday = :birthday where user_id = :user_id";
    private static final String DELETE_USER = "delete from users where user_id = :user_id";
    private static final String SELECT_USER_BY_ID = "select user_id, email, login, user_name, birthday " +
            "from users where user_id = :user_id";
    private static final String SELECT_ALL_USERS = "select user_id, email, login, user_name, birthday from users";

    private static final String ADD_FRIEND = "insert into friends (user_id, friend_id) " +
            "values (:user_id, :friend_id)";
    private static final String DELETE_FRIEND = "delete from friends where user_id = :user_id and " +
            "friend_id = :friend_id";
    private static final String SELECT_COMMON_FRIENDS = "select u.user_id, u.email, u.login, u.user_name, " +
            "u.birthday " +
            "from users as u " +
            "inner join friends as f1 " +
            "on u.user_id = f1.friend_id and f1.user_id = :user_id " +
            "inner join friends as f2 " +
            "on u.user_id = f2.friend_id and f2.user_id = :friend_id";
    private static final String SELECT_FRIENDS_OF_USER = "select u.user_id, u.email, u.login, u.user_name, " +
            "u.birthday " +
            "from users as u " +
            "inner join friends as f " +
            "on u.user_id = f.friend_id and f.user_id = :user_id";

    private static final String DELETE_ALL = "delete from users";

    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    public void save(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("email", user.getEmail());
        map.addValue("login", user.getLogin());
        map.addValue("user_name", user.getName());
        map.addValue("birthday", user.getBirthday());
        jdbcOperations.update(INSERT_USER, map, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    @Override
    public void update(User user) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("user_id", user.getId());
        map.addValue("email", user.getEmail());
        map.addValue("login", user.getLogin());
        map.addValue("user_name", user.getName());
        map.addValue("birthday", user.getBirthday());
        jdbcOperations.update(UPDATE_USER, map);
    }

    @Override
    public void delete(User user) {
        jdbcOperations.update(DELETE_USER, Map.of("user_id", user.getId()));
    }

    @Override
    public Optional<User> getById(long id) {
        final List<User> users = jdbcOperations.query(SELECT_USER_BY_ID, Map.of("user_id", id),
                (rs, i) -> new User(rs.getLong("user_id"),
                        rs.getString("email"),
                        rs.getString("login"),
                        rs.getString("user_name"),
                        rs.getDate("birthday").toLocalDate()));
        if (users.size() != 1 || users.isEmpty()) {
            log.info("Пользователь с идентификатором {} не найден.", id);
            return Optional.empty();
        }
        return Optional.of(users.get(0));
    }

    @Override
    public List<User> getAllUsers() {
        return jdbcOperations.query(SELECT_ALL_USERS, (rs, i) -> new User(rs.getLong("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("user_name"),
                rs.getDate("birthday").toLocalDate()));
    }

    @Override
    public void addFriend(User user, User friend) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("user_id", user.getId());
        map.addValue("friend_id", friend.getId());
        jdbcOperations.update(ADD_FRIEND, map, keyHolder);
    }

    @Override
    public void deleteFriend(User user, User friend) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("user_id", user.getId());
        map.addValue("friend_id", friend.getId());
        jdbcOperations.update(DELETE_FRIEND, map);
    }

    @Override
    public List<User> getAllCommonFriends(User user, User friend) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("user_id", user.getId());
        map.addValue("friend_id", friend.getId());
        return jdbcOperations.query(SELECT_COMMON_FRIENDS, map, (rs, i) -> new User(rs.getLong("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("user_name"),
                rs.getDate("birthday").toLocalDate()));
    }

    @Override
    public List<User> getFriends(User user) {
        return jdbcOperations.query(SELECT_FRIENDS_OF_USER, Map.of("user_id", user.getId()),
                (rs, i) -> new User(rs.getLong("user_id"),
                        rs.getString("email"),
                        rs.getString("login"),
                        rs.getString("user_name"),
                        rs.getDate("birthday").toLocalDate()));
    }

    public void deleteAll() {
        jdbcOperations.update(DELETE_ALL, Map.of());
    }
}


