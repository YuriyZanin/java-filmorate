package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.AlreadyExistException;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.filmorate.util.ValidationUtils.validate;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 0;

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        validate(user, log);
        if (user.getId() == null){
            user.setId(++nextId);
        }
        if (users.containsKey(user.getId())) {
            String message = "Пользователь " + user.getEmail() + " уже зарегистрирован.";
            log.error(message);
            throw new AlreadyExistException(message);
        }
        log.info("Регистрация пользователя {}", user);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User put(@RequestBody User user) {
        validate(user, log);
        if (user.getId() == null || !users.containsKey(user.getId())) {
            String message = "Пользователь " + user.getEmail() + " не найден в базе.";
            log.info(message);
            throw new NotFoundException(message);
        }
        log.info("Обновление пользователя {}", user);
        users.put(user.getId(), user);
        return user;
    }
}
