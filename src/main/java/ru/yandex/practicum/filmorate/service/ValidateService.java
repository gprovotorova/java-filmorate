package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

public class ValidateService {
    public void userNameValidation(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
    }
}
