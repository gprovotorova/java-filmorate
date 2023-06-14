package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

@Component
public class ValidateService {
    public void userNameValidation(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
    }
}
