package ru.yandex.practicum.filmorate.storage.film.mapper;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.util.exeption.ValidationException;

import javax.sql.rowset.serial.SerialArray;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class FilmMapper {
    public static List<Film> makeFilmList(SqlRowSet rs) {
        List<Film> films = new LinkedList<>();
        while (rs.next()) {
            Long filmId = rs.getLong("FILM_ID");
            String filmName = rs.getString("FILM_NAME");
            LocalDate releaseDate = rs.getDate("RELEASE_DATE").toLocalDate();
            int duration = rs.getInt("DURATION");
            String description = rs.getString("DESCRIPTION");
            Rating rating = makeRating(rs);

            Film film = Film.builder()
                    .id(filmId)
                    .name(filmName)
                    .releaseDate(releaseDate)
                    .duration(duration)
                    .mpa(rating)
                    .description(description)
                    .build();
            film.getWhoLikedUserIds().addAll(makeWhoLikedUsers(rs));
            film.getGenres().addAll(makeGenres(rs));
            films.add(film);
        }
        return films;
    }

    public static Rating makeRating(SqlRowSet rs) {
        return Rating.builder()
                .id(rs.getLong("RATING_ID"))
                .name(rs.getString("RATING_NAME"))
                .build();
    }

    public static Set<Long> makeWhoLikedUsers(SqlRowSet rs) {
        Set<Long> whoLikesUserIds = new HashSet<>();
        try {
            SerialArray likesIds = (SerialArray) rs.getObject("WHO_LIKED_USERS_IDS");
            Object[] array = (Object[]) likesIds.getArray();
            for (Object id : array) {
                if (id != null) {
                    Long userId = Long.parseLong(id.toString());
                    whoLikesUserIds.add(userId);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return whoLikesUserIds;
    }

    public static Set<Genre> makeGenres(SqlRowSet rs) {
        final Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));
        try {
            SerialArray genreIds = (SerialArray) rs.getObject("GENRE_IDS");
            SerialArray genreNames = (SerialArray) rs.getObject("GENRE_NAMES");
            Object[] ids = (Object[]) genreIds.getArray();
            Object[] names = (Object[]) genreNames.getArray();
            for (int i = 0; i < ids.length; i++) {
                if (ids[i] != null) {
                    Long genreId = Long.parseLong(ids[i].toString());
                    String genreName = names[i].toString();
                    genres.add(Genre.builder().id(genreId).name(genreName).build());
                }
            }
        } catch (SQLException e) {
            throw new ValidationException("Ошибка преобразования жанров");
        }
        return genres;
    }
}
