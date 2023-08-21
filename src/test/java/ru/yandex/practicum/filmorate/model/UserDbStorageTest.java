package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.List;

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
        User user = new User("malina@mail.ru", "malina", "Алина Михайлова",
                LocalDate.of(2004, 2, 1));
        userStorage.save(user);
        assertEquals(user, userStorage.getById(1).get());
    }

    @DisplayName("проверка обновления и возвращения пользователя")
    @Test
    @Order(2)
    public void updateAndGetUserById() {
        User user = new User("petrov@mail.ru", "new user", "Иван Петров",
                LocalDate.of(1991, 5, 2));
        userStorage.save(user);

        user.setLogin("petrov");
        userStorage.update(user);
        assertEquals(user, userStorage.getById(2).get());
    }

    @DisplayName("проверка удаления пользователя")
    @Test
    @Order(3)
    public void deleteUserById() {
        User userUpdate = userStorage.getById(2).get();
        userStorage.delete(userUpdate);
        assertTrue(userStorage.getById(2).isEmpty());
    }

    @DisplayName("проверка возвращения пользователей")
    @Test
    @Order(4)
    public void getAllUsers() {
        User firstUser = userStorage.getById(1).get();
        User secondUser = new User("hello@mail.ru", "hello", "Мария Иванова",
                LocalDate.of(2000, 8, 5));
        userStorage.save(secondUser);
        User thirdUser = new User("petrov@mail.ru", "petrov", "Иван Петров",
                LocalDate.of(1991, 5, 2));
        userStorage.save(thirdUser);
        List<User> users = List.of(firstUser, secondUser, thirdUser);
        assertNotNull(userStorage.getAllUsers());
        assertEquals(users, userStorage.getAllUsers());
    }

    @DisplayName("проверка добавления и удаления друзей")
    @Test
    @Order(5)
    public void addAndDeleteFriend() {
        User firstUser = userStorage.getById(1).get();
        User thirdUser = userStorage.getById(3).get();

        userStorage.addFriend(firstUser, thirdUser);
        assertEquals(List.of(thirdUser), userStorage.getFriends(firstUser));

        userStorage.deleteFriend(firstUser, thirdUser);
        assertTrue(userStorage.getFriends(firstUser).isEmpty());
    }

    @DisplayName("проверка вывода общих друзей")
    @Test
    @Order(6)
    public void getCommonFriends() {
        User firstUser = userStorage.getById(1).get();
        User secondUser = userStorage.getById(3).get();
        User thirdUser = userStorage.getById(4).get();

        userStorage.addFriend(firstUser, secondUser);
        userStorage.addFriend(thirdUser, secondUser);
        assertEquals(List.of(secondUser), userStorage.getAllCommonFriends(thirdUser, firstUser));
    }
}
