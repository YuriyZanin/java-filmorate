package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.event.mapper.EventMapper;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.exeption.NotFoundException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, EventStorage eventStorage) {
        this.userStorage = userStorage;
        this.eventStorage = eventStorage;
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
        userStorage.update(user);

        eventStorage.create(EventMapper.toEvent(user, friend, EventType.FRIEND, Operation.ADD, LocalDateTime.now()));
        return user;
    }

    public User addFriendWithConfirm(Long userId, Long friendId) {
        User user = get(userId);
        User friend = get(friendId);

        user.getFriendIds().add(friend.getId());
        friend.getFriendIds().add(userId);

        userStorage.update(friend);
        userStorage.update(user);

        eventStorage.create(EventMapper.toEvent(user, friend, EventType.FRIEND, Operation.ADD, LocalDateTime.now()));
        eventStorage.create(EventMapper.toEvent(friend, user, EventType.FRIEND, Operation.ADD, LocalDateTime.now()));
        return user;
    }

    public User removeFriend(Long userId, Long friendId) {
        User user = get(userId);
        User friend = get(friendId);

        user.getFriendIds().remove(friendId);

        userStorage.update(friend);
        userStorage.update(user);

        eventStorage.create(EventMapper.toEvent(user, friend, EventType.FRIEND, Operation.REMOVE, LocalDateTime.now()));
        return user;
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

    public void delete(Long id) {
        userStorage.delete(id);
    }

    public Collection<Event> getFeed(Long userId) {
        get(userId);
        return eventStorage.getFeedList(userId);
    }
}
