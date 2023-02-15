package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {

    Review create(Review review);

    Review update(Review review);

    boolean delete(Long id);

    Optional<Review> get(Long id);

    Collection<Review> getAllWithLimit(Integer count);

    Collection<Review> getByFilm(Long filmId, Integer count);

    boolean addLike(Long id, Long userId);

    boolean addDislike(Long id, Long userId);

    boolean removeLike(Long id, Long userId);

    boolean removeDislike(Long id, Long userId);
}
