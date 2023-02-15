package ru.yandex.practicum.filmorate.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.dto.UserDto;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.mapper.UserMapper;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.util.ValidationUtil.checkErrors;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<UserDto> findAll() {
        return userService.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        return UserMapper.toUserDto(userService.get(id));
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDetails, BindingResult errors) {
        checkErrors(errors);
        return UserMapper.toUserDto(userService.create(UserMapper.toUser(userDetails)));
    }

    @PutMapping
    public UserDto put(@Valid @RequestBody UserDto userDetails, BindingResult errors) {
        checkErrors(errors);
        return UserMapper.toUserDto(userService.update(UserMapper.toUser(userDetails)));
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public UserDto addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return UserMapper.toUserDto(userService.addFriend(id, friendId));
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public UserDto removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return UserMapper.toUserDto(userService.removeFriend(id, friendId));
    }

    @PutMapping("/{id}/friends/{friendId}/confirm")
    public UserDto confirmFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return UserMapper.toUserDto(userService.addFriendWithConfirm(id, friendId));
    }

    @GetMapping("/{id}/friends")
    public Collection<UserDto> findFriends(@PathVariable Long id) {
        return userService.getFriends(id).stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<UserDto> findCommon(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.getCommonFriends(id, otherId).stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }
}
