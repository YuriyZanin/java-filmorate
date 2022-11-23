package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    private final String name;
    private final LocalDate releaseDate;
    private final Duration duration;
    private Integer id;
    private String description;
}
