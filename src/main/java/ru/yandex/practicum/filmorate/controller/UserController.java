package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
@Validated
public class UserController {
    private static final ValidateService validateService = new ValidateService();
    private static final UserRepository userRepository = new UserRepository();
    private static final Map<Long, User> users = userRepository.getUsersFromRepository();

    @GetMapping
    public List<User> getUsers() {
        log.info("Текущее количество пользователей: {} + getUsers", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        user.generateId(users);
        log.info("Создание пользователя: {} - create", user);
        validateService.userNameValidation(user);
        userRepository.save(user);
        log.info("Пользователь " + user.getId() + " создан: {} - create", user);
        return user;
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        userRepository.checkId(user);
        log.info("Обновление данных пользователя " + user.getId() + " : {} - put", user);
        validateService.userNameValidation(user);
        userRepository.save(user);
        log.info("Данные пользователя " + user.getId() + " обновлены: {} - put", user);
        return user;
    }
}
