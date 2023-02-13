package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.util.Collection;

import static ru.yandex.practicum.filmorate.util.ValidationUtil.checkErrors;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film get(@PathVariable Long id) {
        return filmService.get(id);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film, BindingResult errors) {
        checkErrors(errors);
        return filmService.create(film);
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film, BindingResult errors) {
        checkErrors(errors);
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film putLike(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> findPopular(@RequestParam(defaultValue = "10", required = false) Integer count) {
        return filmService.getPopular(count);
    }

    @GetMapping("/common")
    public Collection<Film> findCommon(@RequestParam Long userId, @RequestParam Long friendId) {
        return filmService.getCommon(userId, friendId);
    }
}
