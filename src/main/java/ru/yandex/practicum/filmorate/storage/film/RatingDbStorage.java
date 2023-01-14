package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.util.exeption.NotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@Slf4j
public class RatingDbStorage implements RatingStorage {
    private final JdbcTemplate jdbcTemplate;

    public RatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Rating findById(Long id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("SELECT * FROM ratings WHERE id = ?", id);

        if (rowSet.next()) {
            return new Rating(
                    rowSet.getLong("id"),
                    rowSet.getString("name"));
        } else {
            String message = "Рейтинг с идентификатором " + id + " не найден.";
            log.info(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public Collection<Rating> findALl() {
        String sql = "SELECT * FROM ratings";
        return jdbcTemplate.query(sql, (rs, rn) -> makeRating(rs));
    }

    private Rating makeRating(ResultSet rs) throws SQLException {
        return new Rating(rs.getLong("id"), rs.getString("name"));
    }
}
