package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    void save(User user);

    void update(User user);

    void delete(User user);

    Optional<User> getById(long id);

    List<User> getAllUsers();

    void addFriend(User user, User friend);

    void deleteFriend(User user, User friend);

    List<User> getAllCommonFriends(User user, User friend);

    List<User> getFriends(User user);
}
