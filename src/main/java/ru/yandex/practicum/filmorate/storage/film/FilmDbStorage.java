package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.util.exeption.NotFoundException;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Optional;

@Component
@Qualifier("filmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final String ALL_FILMS_QUERY = "SELECT f.id AS film_id,\n" +
            "       f.name AS film_name,\n" +
            "       f.release_date,\n" +
            "       f.duration,\n" +
            "       f.description,\n" +
            "       f.rating_id,\n" +
            "       r.name AS rating_name,\n" +
            "       array_agg(genre_id) AS genre_ids,\n" +
            "       array_agg(g.name) AS genre_names,\n" +
            "       array_agg(fwlu.who_liked_user_id) AS who_liked_users_ids,\n" +
            "       array_agg(d.id) AS director_ids,\n" +
            "       array_agg(d.name) AS director_names\n" +
            "FROM films f\n" +
            "         JOIN ratings r ON r.id = f.rating_id\n" +
            "         LEFT JOIN film_genres fg ON fg.film_id = f.id\n" +
            "         LEFT JOIN genres g ON g.id = fg.genre_id\n" +
            "         LEFT JOIN film_who_liked_users fwlu ON fwlu.film_id = f.id\n" +
            "         LEFT JOIN film_directors fd ON fd.film_id = f.id\n" +
            "         LEFT JOIN directors d ON d.id = fd.director_id\n";

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> getAll() {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(ALL_FILMS_QUERY + "GROUP BY f.id");
        return FilmMapper.makeFilmList(sqlRowSet);
    }

    @Override
    public Film create(Film film) {
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
        updateGenresLikesDirectors(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE films " +
                "SET duration = ?, description = ?, name = ?, release_date = ?, rating_id = ? WHERE id = ?";
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

        updateGenresLikesDirectors(film);
        log.info("Фильм {} обновлен", film.getId());
        return film;
    }

    @Override
    public Optional<Film> get(Long id) {
        String query = ALL_FILMS_QUERY + "WHERE f.id = ? GROUP BY f.id";

        try {
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(query, id);
            return FilmMapper.makeFilmList(sqlRowSet).stream().findAny();
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Film> getByUser(Long userId) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(
                ALL_FILMS_QUERY +
                        "WHERE fwlu.who_liked_user_id = ?\n" +
                        "GROUP BY f.id;", userId);

        return FilmMapper.makeFilmList(rowSet);
    }

    @Override
    public Collection<Film> getByDirector(Long directorId) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(ALL_FILMS_QUERY +
                "WHERE d.id = ?\n" +
                "GROUP BY f.id;\n", directorId);
        return FilmMapper.makeFilmList(rowSet);
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM films WHERE id = ?", id);
    }

    @Override
    public Collection<Film> getCommon(Long userId, Long friendId) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(
                ALL_FILMS_QUERY +
                        "WHERE f.id IN\n" +
                        "(SELECT l1.film_id FROM film_who_liked_users l1 WHERE l1.who_liked_user_id = ?\n" +
                        "INTERSECT\n" +
                        "SELECT l2.film_id FROM film_who_liked_users l2 WHERE l2.who_liked_user_id = ?)" +
                        "GROUP BY f.id", userId, friendId);
        return FilmMapper.makeFilmList(rowSet);
    }

    private void updateGenresLikesDirectors(Film film) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        jdbcTemplate.update("DELETE FROM film_who_liked_users WHERE film_id = ?", film.getId());
        jdbcTemplate.update("DELETE FROM film_directors WHERE film_id = ?", film.getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) " +
                                "VALUES (?,?)",
                        film.getId(),
                        genre.getId());
            }
        }

        if (film.getWhoLikedUserIds() != null && !film.getWhoLikedUserIds().isEmpty()) {
            for (Long userId : film.getWhoLikedUserIds()) {
                if (userId != null) {
                    jdbcTemplate.update("INSERT INTO film_who_liked_users(film_id, who_liked_user_id)" +
                                    "VALUES (?,?)",
                            film.getId(),
                            userId);
                }
            }
        }

        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            for (Director director : film.getDirectors()) {
                jdbcTemplate.update("INSERT INTO film_directors (DIRECTOR_ID, FILM_ID) " +
                        "VALUES (?,?)", director.getId(), film.getId());
            }
        }
    }
}
