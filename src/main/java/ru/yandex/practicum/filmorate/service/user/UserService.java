package ru.yandex.practicum.filmorate.service.user;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User get(Long id) {
        return userStorage.get(id);
    }

    public User addFriend(Long userId, Long friendId) {
        User user = userStorage.get(userId);
        User friend = userStorage.get(friendId);

        user.getFriends().add(friend.getId());
        friend.getFriends().add(user.getId());

        userStorage.update(friend);
        return userStorage.update(user);
    }

    public User removeFriend(Long userId, Long friendId) {
        User user = userStorage.get(userId);
        User friend = userStorage.get(friendId);

        user.getFriends().remove(friend.getId());
        friend.getFriends().remove(user.getId());

        userStorage.update(friend);
        return userStorage.update(user);
    }

    public Collection<User> getFriends(Long userId) {
        User user = userStorage.get(userId);
        return user.getFriends().stream().map(userStorage::get).collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        User user = userStorage.get(userId);
        User other = userStorage.get(otherId);
        return user.getFriends().stream()
                .filter(id -> other.getFriends().contains(id))
                .map(userStorage::get)
                .collect(Collectors.toList());
    }
}
