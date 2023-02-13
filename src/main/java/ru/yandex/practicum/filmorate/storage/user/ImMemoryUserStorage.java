package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.exeption.AlreadyExistException;
import ru.yandex.practicum.filmorate.util.exeption.NotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class ImMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long nextId = 0;

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        if (user.getId() == null) {
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

    @Override
    public User update(User user) {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            String message = "Пользователь " + user.getEmail() + " не найден в базе.";
            log.info(message);
            throw new NotFoundException(message);
        }
        log.info("Обновление пользователя {}", user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> get(Long id) {
        User user = users.get(id);
        if (user == null) {
            String message = "Пользователь с id " + id + " не найден в базе";
            log.error(message);
            throw new NotFoundException(message);
        }
        log.info("Запрос пользователя {}", user);
        return Optional.of(user);
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        return null;
    }
}
