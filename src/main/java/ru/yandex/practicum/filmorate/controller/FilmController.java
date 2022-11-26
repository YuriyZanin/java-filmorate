package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.ValidationUtil;
import ru.yandex.practicum.filmorate.util.exeption.AlreadyExistException;
import ru.yandex.practicum.filmorate.util.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.util.exeption.ValidationException;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int nextId = 0;

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film, BindingResult errors) {
        if (errors.hasErrors()) {
            String message = ValidationUtil.buildErrorMessage(errors.getFieldErrors());
            log.error(message);
            throw new ValidationException(message);
        }
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

    @PutMapping
    public Film put(@Valid @RequestBody Film film, BindingResult errors) {
        if (errors.hasErrors()) {
            String message = ValidationUtil.buildErrorMessage(errors.getFieldErrors());
            log.error(message);
            throw new ValidationException(message);
        }
        if (!films.containsKey(film.getId())) {
            String message = "Фильм не зарегистрирован";
            log.error(message);
            throw new NotFoundException(message);
        }
        log.info("Обновление фильма {}", film);
        films.put(film.getId(), film);
        return film;
    }
}
