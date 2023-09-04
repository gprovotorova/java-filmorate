package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.filmorate.validator.LoginConstraint;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class User {

    private long id;
    @Email(message = "Некорректный адрес электронной почты.")
    @NotBlank(message = "Адрес электронной почты не может быть пустой.")
    private String email;
    @LoginConstraint
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть позже сегодняшней даты.")
    private LocalDate birthday;

    public User(long id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public User() {
    }
}
