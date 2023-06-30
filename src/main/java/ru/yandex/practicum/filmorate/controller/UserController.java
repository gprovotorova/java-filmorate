package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
@Validated
public class UserController {

    @Autowired
    private ValidateService validateService;

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        log.debug("+ getUsers: {}", userService.getAllUsers().size());
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable int userId) {
        log.debug("+ getUserById: {}", userId);
        return userService.getById(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody @Valid User user) {
        log.debug("+ create: {}", user);
        validateService.userNameValidation(user);
        userService.saveUser(user);
        log.debug("+ create: {}", user);
        return user;
    }

    @PutMapping
    public User put(@RequestBody @Valid User user) {
        log.debug("+ put: {}", user);
        validateService.userNameValidation(user);
        User saved = userService.updateUser(user);
        log.debug("+ put: {}", user);
        return saved;
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable("userId") int userId) {
        User user = userService.getById(userId);
        log.debug("+ delete: {}", user);
        userService.deleteUser(user);
        log.debug("+ delete: {}", user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void putFriend(@PathVariable("id") int userId, @PathVariable("friendId") int friendId) {
        User user = userService.getById(userId);
        User friend = userService.getById(friendId);
        log.debug("+ putFriend: {}", friend);
        userService.addFriend(user, friend);
        log.debug("+ putFriend: {}", friend);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") int userId, @PathVariable("friendId") int friendId) {
        User user = userService.getById(userId);
        User friend = userService.getById(friendId);
        log.debug("+ deleteFriend: {}", friend);
        userService.deleteFriend(user, friend);
        log.debug("+ deleteFriend: {}", friend);
    }

    @GetMapping(value = "/{id}/friends")
    public List<User> getFriends(@PathVariable("id") String userId) {
        User user = userService.getById(Integer.parseInt(userId));
        log.debug("+ getFriends: {}", user);
        List<User> friends = userService.getFriends(user);
        log.debug("+ getFriends: {}", userService.getFriends(user));
        return friends;
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") String userId, @PathVariable("otherId") String friendId) {
        User user = userService.getById(Integer.parseInt(userId));
        User friend = userService.getById(Integer.parseInt(friendId));
        log.debug("+ getCommonFriends: {} and {}", user, friend);
        List<User> commonFriends = userService.getAllCommonFriends(user, friend);
        log.debug("+ getCommonFriends: {}", userService.getAllCommonFriends(user, friend));
        return commonFriends;
    }
}
