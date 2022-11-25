package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.ValidationUtil;
import ru.yandex.practicum.filmorate.util.exeption.AlreadyExistException;
import ru.yandex.practicum.filmorate.util.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.util.exeption.ValidationException;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
    public User create(@Valid @RequestBody User user, BindingResult errors) {
        if (errors.hasErrors()) {
            String message = ValidationUtil.buildErrorMessage(errors.getFieldErrors());
            log.error(message);
            throw new ValidationException(message);
        }
        if (user.getId() == null) {
            user.setId(++nextId);
        }
        if (users.containsKey(user.getId())) {
            String message = "Пользователь " + user.getEmail() + " уже зарегистрирован.";
            log.error(message);
            throw new AlreadyExistException(message);
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        log.info("Регистрация пользователя {}", user);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User put(@Valid @RequestBody User user, BindingResult errors) {
        if (errors.hasErrors()) {
            String message = ValidationUtil.buildErrorMessage(errors.getFieldErrors());
            log.error(message);
            throw new ValidationException(message);
        }
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
