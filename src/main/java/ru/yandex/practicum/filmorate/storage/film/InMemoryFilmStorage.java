package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.exeption.AlreadyExistException;
import ru.yandex.practicum.filmorate.util.exeption.NotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
    public Film save(Film film) {
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
    public Film get(Long id) {
        Film film = films.get(id);
        if (film == null){
            String message = "Фильм с id " + id +" не найден в базе";
            log.error(message);
            throw new NotFoundException(message);
        }
        log.info("Запрос фильма {}", film);
        return film;
    }

    @Override
    public Collection<Film> getByUser(Long userId) {
        return null;
    }
}
