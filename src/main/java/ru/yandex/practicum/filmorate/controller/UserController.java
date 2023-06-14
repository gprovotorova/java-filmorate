package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
@Validated
public class UserController {
    @Autowired
    private ValidateService validateService;
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

    @GetMapping
    public List<User> getUsers() {
        log.debug("getUsers {}", users);
        log.info("Текущее количество пользователей: {} + getUsers", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        log.debug("+ create: {}", user);
        user.generateId(users);
        log.info("Создание пользователя: {}", user);
        validateService.userNameValidation(user);
        save(user);
        log.info("Пользователь " + user.getId() + " создан: {}", user);
        log.debug("+ create: {}", user);
        return user;
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        log.debug("+ put: {}", user);
        checkId(user);
        log.info("Обновление данных пользователя " + user.getId() + " : {}", user);
        validateService.userNameValidation(user);
        save(user);
        log.info("Данные пользователя " + user.getId() + " обновлены: {}", user);
        log.debug("+ put: {}", user);
        return user;
    }
}
