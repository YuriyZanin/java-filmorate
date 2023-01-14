package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.exeption.NotFoundException;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Qualifier("userDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> getAll() {
        String sqlQuery = "SELECT * FROM users";

        Map<Long, Set<Long>> userFriends = new HashMap<>();
        jdbcTemplate.query("SELECT * FROM friendships", (rs -> {
            long userId = rs.getLong("user_id");
            long friendId = rs.getLong("friend_id");
            userFriends.putIfAbsent(userId, new HashSet<>());
            userFriends.get(userId).add(friendId);
        }));

        List<User> users = jdbcTemplate.query(sqlQuery, ((rs, rowNum) -> makeUser(rs)));
        users.forEach(user -> {
            Set<Long> friendIds = userFriends.get(user.getId());
            if (friendIds != null) {
                user.getFriendIds().addAll(friendIds);
            }
        });
        return users;
    }

    @Override
    public User create(User user) {
        String sqlQuery = "INSERT INTO users(email, login, name, birthday) " +
                "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        long userId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        user.setId(userId);
        updateFriends(user);
        log.info("Пользователь {} создан с идентификатором {}", user.getEmail(), userId);

        return get(userId);
    }

    @Override
    public User update(User user) {
        String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        int updatedRows = jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        if (updatedRows == 0) {
            String message = "Пользователь " + user.getEmail() + " не найден в базе.";
            log.info(message);
            throw new NotFoundException(message);
        }

        updateFriends(user);
        log.info("Пользователь {} обновлен", user.getEmail());
        return get(user.getId());
    }

    @Override
    public User get(Long id) {
        Set<Long> friendIds = new HashSet<>();
        jdbcTemplate.query("SELECT * FROM friendships WHERE user_id = ?", (rs -> {
            long friend_id = rs.getLong("friend_id");
            friendIds.add(friend_id);
        }), id);

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE id = ?", id);
        if (sqlRowSet.next()) {
            User user = new User(
                    sqlRowSet.getString("email"),
                    sqlRowSet.getString("login"),
                    Objects.requireNonNull(sqlRowSet.getDate("birthday")).toLocalDate());

            user.setId(sqlRowSet.getLong("id"));
            user.setName(sqlRowSet.getString("name"));
            user.getFriendIds().addAll(friendIds);

            log.info("Найден пользователь: {} {}", user.getId(), user.getEmail());
            return user;
        } else {
            String message = "Пользователь с идентификатором " + id + " не найден.";
            log.info(message);
            throw new NotFoundException(message);
        }
    }

    private User makeUser(ResultSet rs) throws SQLException {
        User user = new User(rs.getString("email"),
                rs.getString("login"),
                rs.getDate("birthday").toLocalDate());
        user.setName(rs.getString("name"));
        user.setId(rs.getLong("id"));
        return user;
    }

    private void updateFriends(User user) {
        jdbcTemplate.update("DELETE FROM friendships WHERE user_id = ?", user.getId());

        if (!user.getFriendIds().isEmpty()) {
            for (Long friendId : user.getFriendIds()) {
                jdbcTemplate.update("INSERT INTO friendships(user_id, friend_id)" +
                                "VALUES (?,?)",
                        user.getId(), friendId
                );
            }
        }
    }
}
