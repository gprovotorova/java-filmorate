package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    public void save(User user) {
        users.put(user.getId(), user);
    }

    public void checkId(User user) {
        for (Long id : users.keySet()) {
            if (id == user.getId()) {
                return;
            }
        }
        throw new ValidationException("Пользователя с таким id не существует.");
    }

    public Map<Long, User> getUsersFromRepository() {
        return users;
    }
}
