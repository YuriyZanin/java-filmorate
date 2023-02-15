package ru.yandex.practicum.filmorate.storage.user.mapper;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.UserDto;

import javax.sql.rowset.serial.SerialArray;
import javax.sql.rowset.serial.SerialException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class UserMapper {
    public static List<User> makeUserList(SqlRowSet rs) {
        final List<User> users = new LinkedList<>();
        while (rs.next()) {
            users.add(makeUser(rs));
        }
        return users;
    }

    public static User makeUser(SqlRowSet rs) {
        return User.builder()
                .id(rs.getLong("USER_ID"))
                .name(rs.getString("USER_NAME"))
                .birthday(rs.getDate("BIRTHDAY").toLocalDate())
                .login(rs.getString("LOGIN"))
                .email(rs.getString("EMAIL"))
                .friendIds(makeFriendIds(rs))
                .build();
    }

    private static Set<Long> makeFriendIds(SqlRowSet rs) {
        Set<Long> friendsIds = new HashSet<>();
        try {
            SerialArray ids = (SerialArray) rs.getObject("FRIENDS_IDS");
            Object[] array = (Object[]) ids.getArray();
            for (Object id : array) {
                if (id != null) {
                    friendsIds.add(Long.parseLong(id.toString()));
                }
            }
        } catch (SerialException e) {
            throw new RuntimeException(e);
        }
        return friendsIds;
    }

    public static User toUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .birthday(userDto.getBirthday())
                .login(userDto.getLogin())
                .email(userDto.getEmail())
                .friendIds(userDto.getFriendIds())
                .build();
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .birthday(user.getBirthday())
                .login(user.getLogin())
                .email(user.getEmail())
                .friendIds(user.getFriendIds())
                .build();
    }
}
