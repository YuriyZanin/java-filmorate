package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.film.RatingDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmoRateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final GenreDbStorage genreDbStorage;
    private final RatingDbStorage ratingDbStorage;

    @Test
    void testFindUserById() {
        userStorage.create(new User("test@mail.com", "login", LocalDate.now()));
        Optional<User> userOptional = Optional.ofNullable(userStorage.get(1L));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void testCreateUser() {
        User created = userStorage.create(new User("test@mail.com", "login", LocalDate.now()));
        Optional<User> userOptional = Optional.ofNullable(userStorage.get(1L));

        assertThat(userOptional)
                .isPresent()
                .hasValue(created);
    }

    @Test
    void testUpdateUser() {
        User created = userStorage.create(new User("test@mail.com", "login", LocalDate.now()));
        created.setName("updated name");
        User updated = userStorage.update(created);

        assertThat(updated)
                .isEqualTo(created)
                .hasFieldOrPropertyWithValue("name", "updated name");
    }

    @Test
    void testFindAllUsers() {
        User user1 = userStorage.create(new User("test@mail.com", "login", LocalDate.now()));
        User user2 = userStorage.create(new User("test2@mail.com", "login2", LocalDate.now()));

        Collection<User> all = userStorage.getAll();
        assertFalse(all.isEmpty());
        assertEquals(2, all.size());
        assertEquals(List.of(user1, user2), all);
    }

    @Test
    void testFindFilmById() {
        filmStorage.save(new Film("test", LocalDate.now(), 100, ratingDbStorage.findById(1L)));
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.get(1L));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L));
    }

    @Test
    void testCreateFilm() {
        Film created = filmStorage.save(new Film("test", LocalDate.now(), 100, ratingDbStorage.findById(1L)));
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.get(1L));

        assertThat(filmOptional)
                .isPresent()
                .hasValue(created);
    }

    @Test
    void testUpdateFilm() {
        Film created = filmStorage.save(new Film("test", LocalDate.now(), 100, ratingDbStorage.findById(1L)));
        created.getGenres().add(genreDbStorage.findById(1L));
        created.setDescription("updated description");
        Film updated = filmStorage.update(created);

        assertThat(updated)
                .isEqualTo(created)
                .hasFieldOrPropertyWithValue("description", "updated description");
    }

    @Test
    void testFindAllFilms() {
        Film test1 = filmStorage.save(new Film("test1", LocalDate.now(), 100, ratingDbStorage.findById(1L)));
        Film test2 = filmStorage.save(new Film("test2", LocalDate.now(), 90, ratingDbStorage.findById(2L)));

        Collection<Film> all = filmStorage.getAll();
        assertFalse(all.isEmpty());
        assertEquals(2, all.size());
        assertEquals(List.of(test1, test2), all);
    }

    @Test
    void testFindGenreById() {
        Optional<Genre> optionalGenre = Optional.of(genreDbStorage.findById(1L));
        assertThat(optionalGenre)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("id", 1L));
    }

    @Test
    void testFindAllGenres() {
        Collection<Genre> all = genreDbStorage.findAll();
        assertFalse(all.isEmpty());
        assertEquals(6, all.size());
    }

    @Test
    void testFindRatingById() {
        Optional<Rating> optionalRating = Optional.of(ratingDbStorage.findById(1L));
        assertThat(optionalRating)
                .isPresent()
                .hasValueSatisfying(rating ->
                        assertThat(rating).hasFieldOrPropertyWithValue("id", 1L));
    }

    @Test
    void testFindAllRatings() {
        Collection<Rating> aLl = ratingDbStorage.findALl();
        assertFalse(aLl.isEmpty());
        assertEquals(5, aLl.size());
    }

    @Test
    void testFindUserFilms() {
        User user = userStorage.create(new User("test@mail.com", "login", LocalDate.now()));
        Collection<Film> userFilms = filmStorage.getByUser(user.getId());
        assertTrue(userFilms.isEmpty());

        Film film = filmStorage.save(new Film("test", LocalDate.now(), 100, ratingDbStorage.findById(1L)));
        film.getWhoLikedUserIds().add(user.getId());
        filmStorage.update(film);
        userFilms = filmStorage.getByUser(user.getId());
        assertEquals(1, userFilms.size());
    }
}