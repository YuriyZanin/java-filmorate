package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.mapper.UserMapper;
import ru.yandex.practicum.filmorate.util.exeption.NotFoundException;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Component
@Qualifier("userDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public static final String ALL_USERS_QUERY = "SELECT u.*, array_agg(f.friend_id) as friends_ids FROM users u\n" +
            "LEFT JOIN friendships f on f.user_id = u.id\n";

    @Override
    public Collection<User> getAll() {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(ALL_USERS_QUERY + "GROUP BY u.id");
        return UserMapper.makeUserList(rowSet);
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

        return user;
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
        return user;
    }

    @Override
    public Optional<User> get(Long id) {
        String query = ALL_USERS_QUERY + "WHERE u.id = ? \n GROUP BY u.id";
        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, id);
            return UserMapper.makeUserList(rowSet).stream().findAny();
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        String query = ALL_USERS_QUERY +
                "WHERE u.id IN (SELECT fr1.friend_id FROM friendships fr1 where fr1.user_id = ?\n" +
                "INTERSECT\n" +
                "SELECT fr2.friend_id FROM friendships fr2 WHERE fr2.user_id = ?)\n" +
                "GROUP BY u.id";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, userId, otherId);
        return UserMapper.makeUserList(rowSet);
    }

    private void updateFriends(User user) {
        jdbcTemplate.update("DELETE FROM friendships WHERE user_id = ?", user.getId());

        if (user.getFriendIds() != null && !user.getFriendIds().isEmpty()) {
            for (Long friendId : user.getFriendIds()) {
                jdbcTemplate.update("INSERT INTO friendships(user_id, friend_id)" +
                                "VALUES (?,?)",
                        user.getId(), friendId
                );
            }
        }
    }
}
