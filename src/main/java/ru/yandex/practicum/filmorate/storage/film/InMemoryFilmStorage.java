package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.exeption.AlreadyExistException;
import ru.yandex.practicum.filmorate.util.exeption.NotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private long nextId = 0;

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        if (film.getId() == null) {
            film.setId(++nextId);
        }
        if (films.containsKey(film.getId())) {
            String message = "Фильм " + film.getName() + " уже зарегистрирован.";
            log.error(message);
            throw new AlreadyExistException(message);
        }
        log.info("Регистрация фильма {}", film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            String message = "Фильм не зарегистрирован";
            log.error(message);
            throw new NotFoundException(message);
        }
        log.info("Обновление фильма {}", film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> get(Long id) {
        Film film = films.get(id);
        log.info("Запрос фильма {}", film);
        if (film != null){
            return Optional.of(film);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Film> getByUser(Long userId) {
        return null;
    }

    @Override
    public Collection<Film> getCommon(Long userId, Long friendId) {
        return null;
    }

    @Override
    public void delete(Long id) {
        films.remove(id);
    }
}
