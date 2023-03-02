package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.film.DirectorStorage;
import ru.yandex.practicum.filmorate.util.exeption.NotFoundException;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Collection<Director> getAll() {
        return directorStorage.getAll();
    }

    public Director get(Long id) {
        return directorStorage.get(id).orElseThrow(() -> new NotFoundException("Режисер не нейден"));
    }

    public Director create(Director director) {
        return directorStorage.create(director);
    }

    public Director update(Director director) {
        Director updated = directorStorage.update(director);
        if (updated == null) {
            throw new NotFoundException("Режисер с id " + director.getId() + " не найден");
        }
        return updated;
    }

    public void delete(Long id) {
        directorStorage.delete(id);
    }
}
