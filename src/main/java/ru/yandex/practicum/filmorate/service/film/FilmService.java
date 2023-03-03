package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.event.mapper.EventMapper;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.util.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.util.exeption.ValidationException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final EventStorage eventStorage;
    private final DirectorService directorService;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       UserService userService,
                       EventStorage eventStorage,
                       DirectorService directorService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.eventStorage = eventStorage;
        this.directorService = directorService;
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        Film update = filmStorage.update(film);
        return get(update.getId());
    }

    public Film get(Long id) {
        return filmStorage.get(id).orElseThrow(() -> new NotFoundException(
                String.format("Фильм с id %s не найден", id)));
    }

    public Film addLike(Long filmId, Long userId) {
        User user = userService.get(userId);
        Film film = get(filmId);

        film.getWhoLikedUserIds().add(user.getId());

        filmStorage.update(film);
        eventStorage.create(EventMapper.toEvent(user, film, EventType.LIKE, Operation.ADD, LocalDateTime.now()));
        return film;
    }

    public Film removeLike(Long filmId, Long userId) {
        User user = userService.get(userId);
        Film film = get(filmId);

        film.getWhoLikedUserIds().remove(user.getId());

        filmStorage.update(film);
        eventStorage.create(EventMapper.toEvent(user, film, EventType.LIKE, Operation.REMOVE, LocalDateTime.now()));
        return film;
    }

    public Collection<Film> getPopular(Integer size) {
        Collection<Film> films = filmStorage.getAll();
        return films.stream()
                .sorted((f1, f2) -> Integer.compare(f2.getWhoLikedUserIds().size(), f1.getWhoLikedUserIds().size()))
                .limit(size)
                .collect(Collectors.toList());
    }

    public Collection<Film> getCommon(Long userId, Long friendId) {
        return filmStorage.getCommon(userId, friendId);
    }

    public void delete(Long id) {
        filmStorage.delete(id);
    }

    public Collection<Film> getByDirectorWithSort(Long directorId, String sortParameter) {
        directorService.get(directorId);
        Collection<Film> films = filmStorage.getByDirector(directorId);
        if (sortParameter.equals("likes")) {
            return films.stream()
                    .sorted(Comparator.comparingInt(f -> f.getWhoLikedUserIds().size())).collect(Collectors.toList());
        }
        if (sortParameter.equals("year")) {
            return films.stream()
                    .sorted(Comparator.comparingInt(f -> f.getReleaseDate().getYear())).collect(Collectors.toList());
        } else {
            throw new ValidationException("Неверный запрос");
        }
    }
}
