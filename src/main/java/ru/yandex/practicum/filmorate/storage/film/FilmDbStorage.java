package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.util.exeption.NotFoundException;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Qualifier("filmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> getAll() {
        List<Film> films = jdbcTemplate.query(
                "SELECT f.id AS film_id, f.name AS film_name, f.description, f.release_date, f.duration, f.rating_id, r.name AS rating_name " +
                "FROM films f " +
                "JOIN ratings r ON r.id = f.rating_id",
                ((rs, rowNum) -> makeFilm(rs)));

        Map<Long, Set<Genre>> filmGenres = new HashMap<>();
        jdbcTemplate.query("SELECT fg.film_id, fg.genre_id, g.name " +
                "FROM film_genres AS fg " +
                "JOIN genres g ON g.id = fg.genre_id", rs -> {
            long filmId = rs.getLong("film_id");
            filmGenres.putIfAbsent(filmId, new HashSet<>());
            filmGenres.get(filmId).add(new Genre(rs.getLong("genre_id"),
                    rs.getString("name")));
        });

        Map<Long, Set<Long>> whoLikedUsers = new HashMap<>();
        jdbcTemplate.query("SELECT * FROM film_who_liked_users", rs -> {
            long filmId = rs.getLong("film_id");
            whoLikedUsers.putIfAbsent(filmId, new HashSet<>());
            whoLikedUsers.get(filmId).add(rs.getLong("who_liked_user_id"));
        });

        films.forEach(film -> {
            Set<Genre> genres = filmGenres.get(film.getId());
            Set<Long> userIds = whoLikedUsers.get(film.getId());
            if (genres != null) {
                film.getGenres().addAll(genres);
            }
            if (userIds != null) {
                film.getWhoLikedUserIds().addAll(userIds);
            }
        });

        return films;
    }

    @Override
    public Film save(Film film) {
        String sqlQuery = "INSERT INTO films(duration, description, name, release_date, rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setInt(1, film.getDuration());
            stmt.setString(2, film.getDescription());
            stmt.setString(3, film.getName());
            stmt.setDate(4, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        long filmId = keyHolder.getKey().longValue();
        film.setId(filmId);
        log.info("Фильм {} сохранен с идентификатором {}", film.getName(), filmId);
        updateGenresAndLikes(film);
        return get(filmId);
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE films SET duration = ?, description = ?, name = ?, release_date = ?, rating_id = ? WHERE id = ?";
        int updatedRows = jdbcTemplate.update(sqlQuery,
                film.getDuration(),
                film.getDescription(),
                film.getName(),
                film.getReleaseDate(),
                film.getMpa().getId(),
                film.getId()
        );

        if (updatedRows == 0) {
            String message = "Фильм " + film.getName() + " не зарегистрирован";
            log.error(message);
            throw new NotFoundException(message);
        }

        updateGenresAndLikes(film);
        log.info("Фильм {} обновлен", film.getId());
        return get(film.getId());
    }

    @Override
    public Film get(Long id) {
        List<Genre> genres = jdbcTemplate.query("SELECT fg.genre_id , g.name " +
                        "FROM film_genres AS fg " +
                        "JOIN genres g ON g.id = fg.genre_id " +
                        "WHERE fg.film_id = ?",
                ((rs, rowNum) -> new Genre(rs.getLong("genre_id"), rs.getString("name"))), id);

        List<Long> whoLikedUserIds = jdbcTemplate.query("SELECT who_liked_user_id FROM film_who_liked_users WHERE film_id = ?",
                (rs, rowNum) -> rs.getLong("who_liked_user_id"), id);

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(
                "SELECT f.id AS film_id, f.name AS film_name, f.release_date, f.duration, f.description, f.rating_id, r.name AS rating_name " +
                "FROM films f " +
                "JOIN ratings r ON r.id = f.rating_id " +
                "WHERE f.id = ?", id);

        if (sqlRowSet.next()) {
            Film film = new Film(
                    sqlRowSet.getString("FILM_NAME"),
                    Objects.requireNonNull(sqlRowSet.getDate("RELEASE_DATE")).toLocalDate(),
                    sqlRowSet.getInt("DURATION"),
                    new Rating(sqlRowSet.getLong("RATING_ID"), sqlRowSet.getString("RATING_NAME"))
            );

            film.setId(sqlRowSet.getLong("FILM_ID"));
            film.setDescription(sqlRowSet.getString("DESCRIPTION"));
            film.getGenres().addAll(genres);
            film.getWhoLikedUserIds().addAll(whoLikedUserIds);

            log.info("Найден фильм: {} {}", film.getId(), film.getName());
            return film;
        } else {
            String message = "Фильм с идентификатором " + id + " не найден.";
            log.info(message);
            throw new NotFoundException(message);
        }
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Film film = new Film(rs.getString("film_name"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                new Rating(rs.getLong("rating_id"), rs.getString("rating_name"))
        );
        film.setId(rs.getLong("film_id"));
        film.setDescription(rs.getString("description"));
        return film;
    }

    private void updateGenresAndLikes(Film film) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        jdbcTemplate.update("DELETE FROM film_who_liked_users WHERE film_id = ?", film.getId());

        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) " +
                                "VALUES (?,?)",
                        film.getId(),
                        genre.getId());
            }
        }

        if (!film.getWhoLikedUserIds().isEmpty()) {
            for (Long userId : film.getWhoLikedUserIds()) {
                jdbcTemplate.update("INSERT INTO film_who_liked_users(film_id, who_liked_user_id)" +
                                "VALUES (?,?)",
                        film.getId(),
                        userId);
            }
        }
    }
}
