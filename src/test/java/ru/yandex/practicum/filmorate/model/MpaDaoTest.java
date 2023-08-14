package ru.yandex.practicum.filmorate.model;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.impl.MpaDaoImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDaoTest {
    @Autowired
    public MpaDaoImpl mpaDao;

    @DisplayName("проверка получения одного рейтинга")
    @Test
    public void getById() {
        Mpa mpaDB = mpaDao.getById(3);
        Mpa mpaCheck = new Mpa(3, "PG-13");
        assertEquals(mpaCheck, mpaDB);
    }

    @DisplayName("проверка получения всех рейтингов")
    @Test
    public void getAll() {
        List<Mpa> mpaDB = mpaDao.getAll();
        List<Mpa> mpaCheck = List.of(new Mpa(1, "G"), new Mpa(2, "PG"), new Mpa(3, "PG-13"),
                new Mpa(4, "R"), new Mpa(5, "NC-17"));
        assertEquals(mpaCheck, mpaDB);
    }
}
