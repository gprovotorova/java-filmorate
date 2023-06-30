package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User saveUser(User user) {
        return userStorage.save(Optional.of(user)).get();
    }

    public User updateUser(User user) {
        userStorage.update(Optional.of(user));
        return user;
    }

    public void deleteUser(User user) {
        userStorage.delete(Optional.of(user));
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(User user, User friend) {
        userStorage.addFriend(Optional.of(user), Optional.of(friend));
    }

    public void deleteFriend(User user, User friend) {
        userStorage.deleteFriend(Optional.of(user), Optional.of(friend));
    }

    public List<User> getAllCommonFriends(User user, User friend) {
        return userStorage.getAllCommonFriends(Optional.of(user), Optional.of(friend));
    }

    public List<User> getFriends(User user) {
        return userStorage.getFriends(Optional.of(user));
    }

    public User getById(long userId) {
        final User user = userStorage.getById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        return user;
    }
}
