package ru.yandex.practicum.filmorate.service.film;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.RatingStorage;

import java.util.Collection;

@Service
public class RatingService {
    private final RatingStorage ratingStorage;

    public RatingService(RatingStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    public Rating get(Long id) {
        return ratingStorage.findById(id);
    }

    public Collection<Rating> getAll() {
        return ratingStorage.findALl();
    }
}
