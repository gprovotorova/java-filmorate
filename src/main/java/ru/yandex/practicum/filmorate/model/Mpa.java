package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
public class Mpa {

    private long id;
    private String name;

    public Mpa(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Mpa() {
    }
}
