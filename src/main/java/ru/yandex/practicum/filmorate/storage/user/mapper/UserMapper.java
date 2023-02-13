package ru.yandex.practicum.filmorate.storage.user.mapper;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.User;

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
            User user = User.builder()
                    .id(rs.getLong("ID"))
                    .name(rs.getString("NAME"))
                    .birthday(rs.getDate("BIRTHDAY").toLocalDate())
                    .login(rs.getString("LOGIN"))
                    .email(rs.getString("EMAIL"))
                    .build();
            user.setFriendIds(makeFriendIds(rs));
            users.add(user);
        }
        return users;
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
}
