package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDbStorageTest {

    @Autowired
    public UserDbStorage userStorage;

    @DisplayName("проверка добавления и возвращения пользователя")
    @Test
    @Order(1)
    public void createAndGetUserById() {
        Optional<User> user = Optional.of(new User("malina@mail.ru", "malina", "Алина Михайлова",
                LocalDate.of(2004, 2, 1)));
        userStorage.save(user);
        assertEquals(user, userStorage.getById(1));
    }

    @DisplayName("проверка обновления и возвращения пользователя")
    @Test
    @Order(2)
    public void updateAndGetUserById() {
        Optional<User> user = Optional.of(new User("petrov@mail.ru", "new user", "Иван Петров",
                LocalDate.of(1991, 5, 2)));
        userStorage.save(user);

        user.get().setLogin("petrov");
        userStorage.update(user);
        assertEquals(user, userStorage.getById(2));
    }

    @DisplayName("проверка удаления пользователя")
    @Test
    @Order(3)
    public void deleteUserById() {
        Optional<User> userUpdate = userStorage.getById(2);
        userStorage.delete(userUpdate);
        assertTrue(userStorage.getById(2).isEmpty());
    }

    @DisplayName("проверка возвращения пользователей")
    @Test
    @Order(4)
    public void getAllUsers() {
        Optional<User> firstUser = userStorage.getById(1);
        Optional<User> secondUser = Optional.of(new User("hello@mail.ru", "hello", "Мария Иванова",
                LocalDate.of(2000, 8, 5)));
        userStorage.save(secondUser);
        Optional<User> thirdUser = Optional.of(new User("petrov@mail.ru", "petrov", "Иван Петров",
                LocalDate.of(1991, 5, 2)));
        userStorage.save(thirdUser);
        List<User> users = List.of(firstUser.get(), secondUser.get(), thirdUser.get());
        assertNotNull(userStorage.getAllUsers());
        assertEquals(users, userStorage.getAllUsers());
    }

    @DisplayName("проверка добавления и удаления друзей")
    @Test
    @Order(5)
    public void addAndDeleteFriend() {
        Optional<User> firstUser = userStorage.getById(1);
        Optional<User> thirdUser = userStorage.getById(3);

        userStorage.addFriend(firstUser, thirdUser);
        assertEquals(List.of(thirdUser.get()), userStorage.getFriends(firstUser));

        userStorage.deleteFriend(firstUser, thirdUser);
        assertTrue(userStorage.getFriends(firstUser).isEmpty());
    }

    @DisplayName("проверка вывода общих друзей")
    @Test
    @Order(6)
    public void getCommonFriends() {
        Optional<User> firstUser = userStorage.getById(1);
        Optional<User> secondUser = userStorage.getById(3);
        Optional<User> thirdUser = userStorage.getById(4);

        userStorage.addFriend(firstUser, secondUser);
        userStorage.addFriend(thirdUser, secondUser);
        assertEquals(List.of(secondUser.get()), userStorage.getAllCommonFriends(thirdUser, firstUser));
    }
}
