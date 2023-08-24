package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Service("userService")
public class UserService {

    private final UserDbStorage userDbStorage;

    @Autowired
    public UserService(UserDbStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    public User getById(long userId) {
        return userDbStorage.getById(userId).orElseThrow(() ->
                new ObjectNotFoundException("Пользователь с id " + userId + " не найден"));
    }

    public User saveUser(User user) {
        userDbStorage.save(user);
        return userDbStorage.getById(user.getId()).orElseThrow(() ->
                new ObjectNotFoundException("Возвращается пустой объект после сохранения пользователя."));
    }

    public User updateUser(User user) {
        userDbStorage.update(user);
        return userDbStorage.getById(user.getId()).orElseThrow(() ->
                new ObjectNotFoundException("Пользователь с id = " + user.getId() + " не найден"));
    }

    public void deleteUser(User user) {
        userDbStorage.delete(user);
    }

    public List<User> getAllUsers() {
        return userDbStorage.getAllUsers();
    }

    public void addFriend(User user, User friend) {
        userDbStorage.addFriend(user, friend);
    }

    public void deleteFriend(User user, User friend) {
        userDbStorage.deleteFriend(user, friend);
    }

    public List<User> getAllCommonFriends(User user, User friend) {
        return userDbStorage.getAllCommonFriends(user, friend);
    }

    public List<User> getFriends(User user) {
        return userDbStorage.getFriends(user);
    }
}
