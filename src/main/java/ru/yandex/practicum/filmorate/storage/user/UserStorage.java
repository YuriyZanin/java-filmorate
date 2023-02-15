package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> getAll();

    User create(User user);

    User update(User user);

    Optional<User> get(Long id);

    Collection<User> getCommonFriends(Long userId, Long otherId);

    void delete(Long id);
}
