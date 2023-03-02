package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorStorage {
    Collection<Director> getAll();

    Optional<Director> get(Long id);

    Director create(Director director);

    Director update(Director director);

    void delete(Long id);
}
