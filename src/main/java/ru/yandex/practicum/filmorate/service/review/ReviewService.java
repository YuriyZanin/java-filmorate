package ru.yandex.practicum.filmorate.service.review;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.util.exeption.NotFoundException;

import java.util.Collection;

@Service
public class ReviewService {
    private ReviewStorage reviewStorage;

    public ReviewService(ReviewStorage reviewStorage) {
        this.reviewStorage = reviewStorage;
    }

    public Review create(Review review) {
        if (review.getUser().getId() < 1 || review.getFilm().getId() < 1) {
            throw new NotFoundException("id фильма или id пользователя задан некорректно");
        }
        review.setUseful(0);
        Review created = reviewStorage.create(review);
        if (created == null) {
            throw new NotFoundException(String.format(
                    "Фильм с id %d или пользователь с id %d не найден",
                    review.getFilm().getId(), review.getUser().getId()));
        }
        return created;
    }

    public void delete(Long id) {
        reviewStorage.delete(id);
    }

    public Review get(Long id) {
        return reviewStorage.get(id).orElseThrow(() -> new NotFoundException("Отзыв не найден"));
    }

    public Review update(Review review) {
        Review updated = reviewStorage.update(review);
        if (updated == null) {
            throw new NotFoundException("Отзыв не найден");
        }
        return updated;
    }

    public Collection<Review> getByFilmOrDefault(Long filmId, Integer count) {
        if (filmId == null) {
            return reviewStorage.getAllWithLimit(count);
        }
        return reviewStorage.getByFilm(filmId, count);
    }

    public void addLike(Long id, Long userId) {
        removeDislike(id, userId);
        reviewStorage.addLike(id, userId);
    }

    public void addDislike(Long id, Long userId) {
        removeLike(id, userId);
        reviewStorage.addDislike(id, userId);
    }

    public void removeLike(Long id, Long userId) {
        reviewStorage.removeLike(id, userId);
    }

    public void removeDislike(Long id, Long userId) {
        reviewStorage.removeDislike(id, userId);
    }
}