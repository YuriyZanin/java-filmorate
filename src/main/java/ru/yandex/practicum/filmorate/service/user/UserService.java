package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Set;
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
        return userStorage.get(id);
    }

    public User addFriend(Long userId, Long friendId) {
        User user = userStorage.get(userId);
        User friend = userStorage.get(friendId);

        user.getFriends().put(friend.getId(), FriendshipStatus.NOT_CONFIRMED);

        userStorage.update(friend);
        return userStorage.update(user);
    }

    public User addConfirmFriend(Long userId, Long friendId) {
        User user = userStorage.get(userId);
        User friend = userStorage.get(friendId);

        user.getFriends().put(friend.getId(), FriendshipStatus.CONFIRMED);
        return userStorage.update(user);
    }

    public User removeFriend(Long userId, Long friendId) {
        User user = userStorage.get(userId);
        User friend = userStorage.get(friendId);

        user.getFriends().remove(friendId);

        userStorage.update(friend);
        return userStorage.update(user);
    }

    public Collection<User> getFriends(Long userId) {
        User user = userStorage.get(userId);
        return user.getFriends().keySet().stream().map(userStorage::get).collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        User user = userStorage.get(userId);
        User other = userStorage.get(otherId);
        Set<Long> userFriends = user.getFriends().keySet();
        Set<Long> otherFriends = other.getFriends().keySet();
        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(userStorage::get)
                .collect(Collectors.toList());
    }

    private void prepareUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}
