package ru.yandex.practicum.filmorate.storage.film.mapper;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.dto.FilmDto;
import ru.yandex.practicum.filmorate.util.exeption.ValidationException;

import javax.sql.rowset.serial.SerialArray;
import java.sql.SQLException;
import java.util.*;

public class FilmMapper {
    public static List<Film> makeFilmList(SqlRowSet rs) {
        List<Film> films = new LinkedList<>();
        while (rs.next()) {
            films.add(makeFilm(rs));
        }
        return films;
    }

    public static Film makeFilm(SqlRowSet rs) {
        return Film.builder()
                .id(rs.getLong("FILM_ID"))
                .name(rs.getString("FILM_NAME"))
                .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                .duration(rs.getInt("DURATION"))
                .description(rs.getString("DESCRIPTION"))
                .mpa(makeRating(rs))
                .whoLikedUserIds(makeWhoLikedUsers(rs))
                .genres(makeGenres(rs))
                .build();
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

    public static Film toFilm(FilmDto filmDto) {
        return Film.builder()
                .id(filmDto.getId())
                .name(filmDto.getName())
                .releaseDate(filmDto.getReleaseDate())
                .duration(filmDto.getDuration())
                .description(filmDto.getDescription())
                .mpa(filmDto.getMpa())
                .whoLikedUserIds(filmDto.getWhoLikedUserIds() != null ? filmDto.getWhoLikedUserIds() : new HashSet<>())
                .genres(doSortingGenres(filmDto.getGenres()))
                .build();
    }

    public static FilmDto toFilmDto(Film film) {
        return FilmDto.builder()
                .id(film.getId())
                .name(film.getName())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .description(film.getDescription())
                .mpa(film.getMpa())
                .whoLikedUserIds(film.getWhoLikedUserIds())
                .genres(film.getGenres())
                .build();
    }

    private static Set<Genre> doSortingGenres(Set<Genre> genres) {
        if (genres == null) {
            return new HashSet<>();
        }
        TreeSet<Genre> sorted = new TreeSet<>(Comparator.comparing(Genre::getId));
        sorted.addAll(genres);
        return sorted;
    }
}
