package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.exeption.NotFoundException;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User create(User user) {
        prepareUser(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        prepareUser(user);
        return userStorage.update(user);
    }

    public User get(Long id) {
        return userStorage.get(id).orElseThrow(() ->
                new NotFoundException("Пользователь с идентификатором " + id + " не найден.")
        );
    }

    public User addFriend(Long userId, Long friendId) {
        User user = get(userId);
        User friend = get(friendId);

        user.getFriendIds().add(friend.getId());

        userStorage.update(friend);
        return userStorage.update(user);
    }

    public User addFriendWithConfirm(Long userId, Long friendId) {
        User user = get(userId);
        User friend = get(friendId);

        user.getFriendIds().add(friend.getId());
        friend.getFriendIds().add(userId);

        userStorage.update(friend);
        return userStorage.update(user);
    }

    public User removeFriend(Long userId, Long friendId) {
        User user = get(userId);
        User friend = get(friendId);

        user.getFriendIds().remove(friendId);

        userStorage.update(friend);
        return userStorage.update(user);
    }

    public Collection<User> getFriends(Long userId) {
        User user = get(userId);
        return user.getFriendIds().stream().map(this::get).collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        return userStorage.getCommonFriends(userId, otherId);
    }

    private void prepareUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}
