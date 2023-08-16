package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Optional<User> save(Optional<User> user);

    Optional<User> update(Optional<User> user);

    void delete(Optional<User> user);

    Optional<User> getById(long id);

    List<User> getAllUsers();

    void addFriend(Optional<User> user, Optional<User> friend);

    void deleteFriend(Optional<User> user, Optional<User> friend);

    List<User> getAllCommonFriends(Optional<User> user, Optional<User> friend);

    List<User> getFriends(Optional<User> user);
}
