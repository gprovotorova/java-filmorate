package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDbStorageTest extends AbstractTest {

    @Autowired
    public UserDbStorage userStorage;

    @BeforeEach
    public void beforeEach() {
        User firstUser = new User("malina@mail.ru", "malina", "Алина Михайлова",
                LocalDate.of(2004, 2, 1));
        userStorage.save(firstUser);
        User secondUser = new User("hello@mail.ru", "hello", "Мария Иванова",
                LocalDate.of(2000, 8, 5));
        userStorage.save(secondUser);
        User thirdUser = new User("petrov@mail.ru", "petrov", "Иван Петров",
                LocalDate.of(1991, 5, 2));
        userStorage.save(thirdUser);
    }

    @AfterEach
    public void afterEach() {
        userStorage.deleteAll();
    }

    @DisplayName("проверка добавления и возвращения пользователя")
    @Test
    @Order(1)
    public void createAndGetUserById() {
        User user = new User("malina@mail.ru", "malina", "Алина Михайлова",
                LocalDate.of(2004, 2, 1));
        userStorage.save(user);
        assertEquals(user, userStorage.getById(4).get());
    }

    @DisplayName("проверка обновления и возвращения пользователя")
    @Test
    @Order(2)
    public void updateAndGetUserById() {
        User user = userStorage.getById(5).get();

        user.setLogin("new user");
        userStorage.update(user);
        assertEquals(user, userStorage.getById(5).get());
    }

    @DisplayName("проверка удаления пользователя")
    @Test
    @Order(3)
    public void deleteUserById() {
        User user = userStorage.getById(8).get();
        userStorage.delete(user);
        assertTrue(userStorage.getById(8).isEmpty());
    }

    @DisplayName("проверка возвращения пользователей")
    @Test
    @Order(4)
    public void getAllUsers() {
        User firstUser = userStorage.getById(11).get();
        User secondUser = userStorage.getById(12).get();
        User thirdUser = userStorage.getById(13).get();
        List<User> users = List.of(firstUser, secondUser, thirdUser);
        assertNotNull(userStorage.getAllUsers());
        assertEquals(users, userStorage.getAllUsers());
    }

    @DisplayName("проверка добавления и удаления друзей")
    @Test
    @Order(5)
    public void addAndDeleteFriend() {
        User firstUser = userStorage.getById(14).get();
        User thirdUser = userStorage.getById(16).get();

        userStorage.addFriend(firstUser, thirdUser);
        assertEquals(List.of(thirdUser), userStorage.getFriends(firstUser));

        userStorage.deleteFriend(firstUser, thirdUser);
        assertTrue(userStorage.getFriends(firstUser).isEmpty());
    }

    @DisplayName("проверка вывода общих друзей")
    @Test
    @Order(6)
    public void getCommonFriends() {
        User firstUser = userStorage.getById(17).get();
        User secondUser = userStorage.getById(18).get();
        User thirdUser = userStorage.getById(19).get();

        userStorage.addFriend(firstUser, secondUser);
        userStorage.addFriend(thirdUser, secondUser);
        assertEquals(List.of(secondUser), userStorage.getAllCommonFriends(thirdUser, firstUser));

        userStorage.deleteFriend(firstUser, secondUser);
        userStorage.deleteFriend(thirdUser, secondUser);
    }
}
