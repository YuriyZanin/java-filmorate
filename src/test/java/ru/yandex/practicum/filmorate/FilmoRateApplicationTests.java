package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.film.RatingDbStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    private final ReviewDbStorage reviewDbStorage;

    @Test
    void testFindUserById() {
        User test = User.builder().email("test@mail.com").login("login").birthday(LocalDate.now()).build();
        userStorage.create(test);
        Optional<User> userOptional = userStorage.get(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("id", 1L));
    }

    @Test
    void testCreateUser() {
        User created = User.builder().email("test@mail.com").login("login").birthday(LocalDate.now()).build();
        userStorage.create(created);
        Optional<User> userOptional = userStorage.get(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValue(created);
    }

    @Test
    void testUpdateUser() {
        User created = User.builder().email("test@mail.com").login("login").birthday(LocalDate.now()).build();
        userStorage.create(created);
        created.setName("updated name");
        User updated = userStorage.update(created);

        assertThat(updated)
                .isEqualTo(created)
                .hasFieldOrPropertyWithValue("name", "updated name");
    }

    @Test
    void testDeleteUser() {
        User created = userStorage.create(
                User.builder().email("test@mail.com").login("login").birthday(LocalDate.now()).build());
        Optional<User> fromDb = userStorage.get(created.getId());
        assertThat(fromDb)
                .isPresent()
                .hasValue(created);

        userStorage.delete(created.getId());
        fromDb = userStorage.get(created.getId());
        assertThat(fromDb)
                .isNotPresent();
    }

    @Test
    void testFindAllUsers() {
        User user1 = User.builder().email("test1@mail.com").login("login1").birthday(LocalDate.now()).build();
        User user2 = User.builder().email("test2@mail.com").login("login2").birthday(LocalDate.now()).build();
        userStorage.create(user1);
        userStorage.create(user2);

        Collection<User> all = userStorage.getAll();
        assertFalse(all.isEmpty());
        assertEquals(2, all.size());
        assertEquals(List.of(user1, user2), all);
    }

    @Test
    void testFindFilmById() {
        Film test = Film.builder().name("test").releaseDate(LocalDate.now()).duration(100).mpa(Rating.builder().id(1L)
                .build()).build();
        filmStorage.create(test);
        Optional<Film> filmOptional = filmStorage.get(1L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L));
    }

    @Test
    void testCreateFilm() {
        Film created = Film.builder()
                .name("test").releaseDate(LocalDate.now()).duration(100).mpa(ratingDbStorage.findById(1L)).build();
        created.getGenres().add(genreDbStorage.findById(1L));
        filmStorage.create(created);
        Optional<Film> filmOptional = filmStorage.get(1L);

        assertThat(filmOptional)
                .isPresent()
                .hasValue(created);
    }

    @Test
    void testUpdateFilm() {
        Film created = Film.builder()
                .name("test").releaseDate(LocalDate.now()).duration(100).mpa(Rating.builder().id(1L).build()).build();
        filmStorage.create(created);
        created.getGenres().add(genreDbStorage.findById(1L));
        created.setDescription("updated description");
        Film updated = filmStorage.update(created);

        assertThat(updated)
                .isEqualTo(created)
                .hasFieldOrPropertyWithValue("description", "updated description");
    }

    @Test
    void testDeleteFilm() {
        Film created = filmStorage.create(Film.builder()
                .name("test").releaseDate(LocalDate.now()).duration(100).mpa(ratingDbStorage.findById(1L)).build());
        Optional<Film> fromDb = filmStorage.get(created.getId());
        assertThat(fromDb)
                .isPresent()
                .hasValue(created);

        filmStorage.delete(created.getId());
        fromDb = filmStorage.get(created.getId());
        assertThat(fromDb)
                .isNotPresent();
    }

    @Test
    void testFindAllFilms() {
        Film test1 = Film.builder().name("test1").releaseDate(LocalDate.now()).duration(100).mpa(ratingDbStorage.findById(1L)).build();
        Film test2 = Film.builder().name("test2").releaseDate(LocalDate.now()).duration(90).mpa(ratingDbStorage.findById(2L)).build();
        filmStorage.create(test1);
        filmStorage.create(test2);
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
                .hasValueSatisfying(rating -> assertThat(rating).hasFieldOrPropertyWithValue("id", 1L));
    }

    @Test
    void testFindAllRatings() {
        Collection<Rating> aLl = ratingDbStorage.findALl();
        assertFalse(aLl.isEmpty());
        assertEquals(5, aLl.size());
    }

    @Test
    void testFindUserFilms() {
        User user = User.builder().email("test@mail.com").login("login").birthday(LocalDate.now()).build();
        userStorage.create(user);
        Collection<Film> userFilms = filmStorage.getByUser(user.getId());
        assertTrue(userFilms.isEmpty());

        Film film = Film.builder()
                .name("test").releaseDate(LocalDate.now()).duration(100).mpa(Rating.builder().id(1L).build()).build();
        filmStorage.create(film);
        film.getWhoLikedUserIds().add(user.getId());
        filmStorage.update(film);
        userFilms = filmStorage.getByUser(user.getId());
        assertEquals(1, userFilms.size());

        Film fromDb = filmStorage.get(film.getId()).orElse(null);
        assertEquals(Set.of(user.getId()), fromDb.getWhoLikedUserIds());
    }

    @Test
    void testCommonFilms() {
        User user1 = User.builder().email("test1@mail.com").login("login1").birthday(LocalDate.now()).build();
        User user2 = User.builder().email("test2@mail.com").login("login2").birthday(LocalDate.now()).build();
        userStorage.create(user1);
        userStorage.create(user2);
        Film film = Film.builder()
                .name("test").releaseDate(LocalDate.now()).duration(100).mpa(Rating.builder().id(1L).build()).build();
        Film save = filmStorage.create(film);
        save.getWhoLikedUserIds().add(user1.getId());
        assertTrue(filmStorage.getCommon(user1.getId(), user2.getId()).isEmpty());

        save.getWhoLikedUserIds().add(user2.getId());
        filmStorage.update(save);
        assertEquals(1, filmStorage.getCommon(user1.getId(), user2.getId()).size());
    }

    @Test
    void testCommonFriends() {
        User user1 = User.builder().email("test1@mail.com").login("login1").birthday(LocalDate.now()).build();
        User user2 = User.builder().email("test2@mail.com").login("login2").birthday(LocalDate.now()).build();
        userStorage.create(user1);
        userStorage.create(user2);
        Collection<User> commonFriends = userStorage.getCommonFriends(user1.getId(), user2.getId());
        assertTrue(commonFriends.isEmpty());
    }

    @Test
    void testCreateReview() {
        User user = userStorage.create(
                User.builder().email("test1@mail.com").login("login1").birthday(LocalDate.now()).build());
        Film film = filmStorage.create(
                Film.builder().name("test1")
                        .releaseDate(LocalDate.now()).duration(100).mpa(ratingDbStorage.findById(1L)).build());
        Review review = reviewDbStorage.create(
                Review.builder().content("test").user(user).film(film).isPositive(true).build());

        Optional<Review> userOptional = reviewDbStorage.get(review.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValue(review);
    }

    @Test
    void testUpdateReview() {
        User user = userStorage.create(
                User.builder().email("test1@mail.com").login("login1").birthday(LocalDate.now()).build());
        Film film = filmStorage.create(
                Film.builder().name("test1")
                        .releaseDate(LocalDate.now()).duration(100).mpa(ratingDbStorage.findById(1L)).build());
        Review review = reviewDbStorage.create(
                Review.builder().content("test").user(user).film(film).isPositive(true).build());

        review.setContent("update content");
        review.setIsPositive(false);
        Review updated = reviewDbStorage.update(review);

        assertThat(updated)
                .isEqualTo(review)
                .hasFieldOrPropertyWithValue("content", "update content")
                .hasFieldOrPropertyWithValue("isPositive", false);
    }

    @Test
    void testFindReviewByFilm() {
        User user = userStorage.create(
                User.builder().email("test1@mail.com").login("login1").birthday(LocalDate.now()).build());
        Film film = filmStorage.create(
                Film.builder().name("test1")
                        .releaseDate(LocalDate.now()).duration(100).mpa(ratingDbStorage.findById(1L)).build());
        Review review = reviewDbStorage.create(
                Review.builder().content("test").user(user).film(film).isPositive(true).build());

        Collection<Review> byFilm = reviewDbStorage.getByFilm(1L, 1);

        assertThat(byFilm)
                .hasSize(1)
                .contains(review);
    }

    @Test
    void testFindReviewByDefault() {
        User user = userStorage.create(
                User.builder().email("test1@mail.com").login("login1").birthday(LocalDate.now()).build());
        Film film = filmStorage.create(
                Film.builder().name("test1")
                        .releaseDate(LocalDate.now()).duration(100).mpa(ratingDbStorage.findById(1L)).build());
        Review review = reviewDbStorage.create(
                Review.builder().content("test").user(user).film(film).isPositive(true).build());

        Collection<Review> byFilmOrDefault = reviewDbStorage.getAllWithLimit(1);

        assertThat(byFilmOrDefault)
                .hasSize(1)
                .contains(review);
    }

    @Test
    void testDeleteReviewById() {
        User user = userStorage.create(
                User.builder().email("test1@mail.com").login("login1").birthday(LocalDate.now()).build());
        Film film = filmStorage.create(
                Film.builder().name("test1")
                        .releaseDate(LocalDate.now()).duration(100).mpa(ratingDbStorage.findById(1L)).build());
        Review review = reviewDbStorage.create(
                Review.builder().content("test").user(user).film(film).isPositive(true).build());

        assertThat(review)
                .isNotNull();

        reviewDbStorage.delete(review.getId());
        Optional<Review> mustBeEmpty = reviewDbStorage.get(review.getId());

        assertThat(mustBeEmpty)
                .isEmpty();
    }

    @Test
    void testAddAndRemoveLikeToReview() {
        User user = userStorage.create(
                User.builder().email("test1@mail.com").login("login1").birthday(LocalDate.now()).build());
        Film film = filmStorage.create(
                Film.builder().name("test1")
                        .releaseDate(LocalDate.now()).duration(100).mpa(ratingDbStorage.findById(1L)).build());
        Review review = reviewDbStorage.create(
                Review.builder().content("test").user(user).film(film).isPositive(true).build());

        boolean isAdded = reviewDbStorage.addLike(review.getId(), user.getId());
        assertTrue(isAdded);

        Optional<Review> afterSave = reviewDbStorage.get(review.getId());
        assertThat(afterSave)
                .isPresent()
                .hasValueSatisfying(r -> assertThat(r).hasFieldOrPropertyWithValue("useful", 1));

        boolean isRemoved = reviewDbStorage.removeLike(review.getId(), user.getId());
        assertTrue(isRemoved);

        afterSave = reviewDbStorage.get(review.getId());
        assertThat(afterSave)
                .isPresent()
                .hasValueSatisfying(r -> assertThat(r).hasFieldOrPropertyWithValue("useful", 0));

        isRemoved = reviewDbStorage.removeLike(review.getId(), user.getId());
        assertFalse(isRemoved);

        afterSave = reviewDbStorage.get(review.getId());
        assertThat(afterSave)
                .isPresent()
                .hasValueSatisfying(r -> assertThat(r).hasFieldOrPropertyWithValue("useful", 0));
    }

    @Test
    void testAddAndRemoveDislikeToReview() {
        User user = userStorage.create(
                User.builder().email("test1@mail.com").login("login1").birthday(LocalDate.now()).build());
        Film film = filmStorage.create(
                Film.builder().name("test1")
                        .releaseDate(LocalDate.now()).duration(100).mpa(ratingDbStorage.findById(1L)).build());
        Review review = reviewDbStorage.create(
                Review.builder().content("test").user(user).film(film).isPositive(true).build());

        boolean isAdded = reviewDbStorage.addDislike(review.getId(), user.getId());
        assertTrue(isAdded);

        Optional<Review> afterSave = reviewDbStorage.get(review.getId());
        assertThat(afterSave)
                .isPresent()
                .hasValueSatisfying(r -> assertThat(r).hasFieldOrPropertyWithValue("useful", -1));

        boolean isRemoved = reviewDbStorage.removeDislike(review.getId(), user.getId());
        assertTrue(isRemoved);

        afterSave = reviewDbStorage.get(review.getId());
        assertThat(afterSave)
                .isPresent()
                .hasValueSatisfying(r -> assertThat(r).hasFieldOrPropertyWithValue("useful", 0));
    }
}