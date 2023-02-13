package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.exeption.NotFoundException;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film create(Film film) {
        return filmStorage.save(film);
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
        User user = userStorage.get(userId);
        Film film = get(filmId);
        film.getWhoLikedUserIds().add(user.getId());
        return filmStorage.update(film);
    }

    public Film removeLike(Long filmId, Long userId) {
        User user = userStorage.get(userId);
        Film film = get(filmId);
        film.getWhoLikedUserIds().remove(user.getId());
        return filmStorage.update(film);
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
}
