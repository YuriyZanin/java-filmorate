package ru.yandex.practicum.filmorate.controller.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.dto.FilmDto;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.mapper.FilmMapper;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

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
    public Collection<FilmDto> findAll() {
        return filmService.getAll().stream().map(FilmMapper::toFilmDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public FilmDto get(@PathVariable Long id) {
        return FilmMapper.toFilmDto(filmService.get(id));
    }

    @PostMapping
    public FilmDto create(@Valid @RequestBody FilmDto filmDetails, BindingResult errors) {
        checkErrors(errors);
        return FilmMapper.toFilmDto(filmService.create(FilmMapper.toFilm(filmDetails)));
    }

    @PutMapping
    public FilmDto put(@Valid @RequestBody FilmDto filmDetails, BindingResult errors) {
        checkErrors(errors);
        return FilmMapper.toFilmDto(filmService.update(FilmMapper.toFilm(filmDetails)));
    }

    @DeleteMapping("/{filmId}")
    public void delete(@PathVariable Long filmId) {
        filmService.delete(filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public FilmDto putLike(@PathVariable Long id, @PathVariable Long userId) {
        return FilmMapper.toFilmDto(filmService.addLike(id, userId));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public FilmDto removeLike(@PathVariable Long id, @PathVariable Long userId) {
        return FilmMapper.toFilmDto(filmService.removeLike(id, userId));
    }

    @GetMapping("/popular")
    public Collection<FilmDto> findPopular(@RequestParam(defaultValue = "10", required = false) Integer count) {
        return filmService.getPopular(count).stream().map(FilmMapper::toFilmDto).collect(Collectors.toList());
    }

    @GetMapping("/common")
    public Collection<FilmDto> findCommon(@RequestParam Long userId, @RequestParam Long friendId) {
        return filmService.getCommon(userId, friendId).stream().map(FilmMapper::toFilmDto).collect(Collectors.toList());
    }
}
