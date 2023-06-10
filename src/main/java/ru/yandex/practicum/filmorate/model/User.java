package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;

import lombok.Data;
import ru.yandex.practicum.filmorate.validator.LoginConstraint;

import java.time.LocalDate;
import java.util.Map;

@Data
public class User {
    private long id = 0;
    @Email(message = "Некорректный адрес электронной почты.")
    @NotBlank(message = "Адрес электронной почты не может быть пустой.")
    private final String email;
    @LoginConstraint
    private final String login;
    private String name;
    @Past
    private final LocalDate birthday;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public long setId() {
        return ++id;
    }

    public void generateId(Map<Long, User> users) {
        id = users.size();
        setId();
    }
}
