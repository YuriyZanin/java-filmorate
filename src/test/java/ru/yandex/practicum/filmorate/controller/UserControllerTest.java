package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exeption.AlreadyExistException;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {

    UserController controller = new UserController();

    @Test
    void findAll() {
        assertEquals(0, controller.findAll().size());
        controller.create(new User("test@email.com", "test", LocalDate.now()));
        assertEquals(1, controller.findAll().size());
    }

    @Test
    void create() {
        User newUser = new User("test@email.com", "test", LocalDate.now());
        User created = controller.create(newUser);
        assertEquals(newUser, created);
        assertThrows(AlreadyExistException.class, () -> controller.create(created));
        assertThrows(ValidationException.class, () -> controller.create(null));
        assertThrows(ValidationException.class, () -> controller.create(new User("test2@email.com", "   ", LocalDate.now())));
        assertThrows(ValidationException.class, () -> controller.create(new User("test2@email.com", null, LocalDate.now())));
        assertThrows(ValidationException.class, () -> controller.create(new User("test3@email.com", "test", LocalDate.now().plusDays(10))));
        assertThrows(ValidationException.class, () -> controller.create(new User("incorrectEmail", "test", LocalDate.now())));
        assertThrows(ValidationException.class, () -> controller.create(new User("", "test", LocalDate.now())));
    }

    @Test
    void put() {
        User user = new User("test@email", "login", LocalDate.now());
        assertThrows(NotFoundException.class, () -> controller.put(user));
        controller.create(user);
        assertEquals(user, controller.put(user));
        user.setId(999);
        assertThrows(NotFoundException.class, () -> controller.put(user));
    }
}