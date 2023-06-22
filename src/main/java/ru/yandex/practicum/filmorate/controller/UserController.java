package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
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
        log.debug("+ save: {}", user);
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
        validateService.userNameValidation(user);
        save(user);
        log.debug("+ create: {}", user);
        return user;
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        log.debug("+ put: {}", user);
        checkId(user);
        validateService.userNameValidation(user);
        save(user);
        log.debug("+ put: {}", user);
        return user;
    }
}
