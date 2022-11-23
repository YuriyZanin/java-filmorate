package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exeption.AlreadyExistException;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    FilmController controller = new FilmController();

    @Test
    void findAll() {
        assertTrue(controller.findAll().isEmpty());
        Film film = new Film("Test", LocalDate.now(), Duration.ofMinutes(90));
        controller.create(film);
        assertEquals(1, controller.findAll().size());
        controller.put(film);
        assertEquals(1, controller.findAll().size());
    }

    @Test
    void create() {
        Film newFilm = new Film("Test", LocalDate.of(1895, Month.DECEMBER, 28), Duration.ofMinutes(90));
        Film created = controller.create(newFilm);
        assertEquals(newFilm, created);
        assertThrows(AlreadyExistException.class, () -> controller.create(created));
        assertThrows(ValidationException.class, () -> controller.create(null));
        assertThrows(ValidationException.class, () -> controller.create(new Film(null, LocalDate.now(), Duration.ofMinutes(200))));
        assertThrows(ValidationException.class, () -> controller.create(new Film("   ", LocalDate.now(), Duration.ofMinutes(200))));
        assertThrows(ValidationException.class, () -> controller.create(new Film("test", LocalDate.now(), Duration.ZERO)));
        assertThrows(ValidationException.class, () -> controller.create(new Film("test", LocalDate.of(1895, Month.DECEMBER, 27), Duration.ofMinutes(90))));
        assertThrows(ValidationException.class, () -> {
            Film tst = new Film("tst", LocalDate.now(), Duration.ofMinutes(200));
            tst.setDescription("t".repeat(201));
            controller.create(tst);
        });
    }

    @Test
    void put() {
        Film newFilm = new Film("test", LocalDate.now(), Duration.ofMinutes(90));
        assertThrows(NotFoundException.class, () -> controller.put(newFilm));
        controller.create(newFilm);
        assertEquals(newFilm, controller.put(newFilm));
        newFilm.setId(999);
        assertThrows(NotFoundException.class, () -> controller.put(newFilm));
    }
}