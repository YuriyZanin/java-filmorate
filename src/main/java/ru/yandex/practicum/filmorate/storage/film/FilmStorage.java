package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> getAll();

    Film create(Film film);

    Film update(Film film);

    Optional<Film> get(Long id);

    Collection<Film> getByUser(Long userId);

    Collection<Film> getCommon(Long userId, Long friendId);

    void delete(Long id);
}
