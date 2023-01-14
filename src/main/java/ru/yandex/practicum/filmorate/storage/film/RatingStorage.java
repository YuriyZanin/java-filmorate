package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.Optional;

public interface RatingStorage {
    Rating findById(Long id);
    Collection<Rating> findALl();
}
