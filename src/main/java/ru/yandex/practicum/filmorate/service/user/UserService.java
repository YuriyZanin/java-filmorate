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
        prepareUser(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        prepareUser(user);
        return userStorage.update(user);
    }

    public User get(Long id) {
        return userStorage.get(id);
    }

    public User addFriend(Long userId, Long friendId) {
        User user = userStorage.get(userId);
        User friend = userStorage.get(friendId);

        user.getFriendIds().add(friend.getId());
        friend.getFriendIds().add(user.getId());

        userStorage.update(friend);
        return userStorage.update(user);
    }

    public User removeFriend(Long userId, Long friendId) {
        User user = userStorage.get(userId);
        User friend = userStorage.get(friendId);

        user.getFriendIds().remove(friend.getId());
        friend.getFriendIds().remove(user.getId());

        userStorage.update(friend);
        return userStorage.update(user);
    }

    public Collection<User> getFriends(Long userId) {
        User user = userStorage.get(userId);
        return user.getFriendIds().stream().map(userStorage::get).collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        User user = userStorage.get(userId);
        User other = userStorage.get(otherId);
        return user.getFriendIds().stream()
                .filter(id -> other.getFriendIds().contains(id))
                .map(userStorage::get)
                .collect(Collectors.toList());
    }

    private void prepareUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}
