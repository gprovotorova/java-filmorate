package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
public class Genres {

    private long id;
    private String name;

    public Genres(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Genres() {
    }
}