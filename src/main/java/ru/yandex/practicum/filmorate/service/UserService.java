package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Service("userService")
public class UserService {

    @Qualifier("userDbStorage")
    private final UserDbStorage userDbStorage;

    @Autowired
    public UserService(UserDbStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    public User getById(long userId) {
        final User user = userDbStorage.getById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id " + userId + " не найден"));
        return user;
    }

    public User saveUser(User user) {
        return userDbStorage.save(Optional.of(user)).get();
    }

    public User updateUser(User user) {
        userDbStorage.update(Optional.of(user));
        return user;
    }

    public void deleteUser(User user) {
        userDbStorage.delete(Optional.of(user));
    }

    public List<User> getAllUsers() {
        return userDbStorage.getAllUsers();
    }

    public void addFriend(User user, User friend) {
        userDbStorage.addFriend(Optional.of(user), Optional.of(friend));
    }

    public void deleteFriend(User user, User friend) {
        userDbStorage.deleteFriend(Optional.of(user), Optional.of(friend));
    }

    public List<User> getAllCommonFriends(User user, User friend) {
        return userDbStorage.getAllCommonFriends(Optional.of(user), Optional.of(friend));
    }

    public List<User> getFriends(User user) {
        return userDbStorage.getFriends(Optional.of(user));
    }
}
