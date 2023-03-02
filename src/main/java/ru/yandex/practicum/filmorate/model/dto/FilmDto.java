package ru.yandex.practicum.filmorate.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.util.validation.MinDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static ru.yandex.practicum.filmorate.util.ValidationUtil.MAX_DESCRIPTION_LENGTH;
import static ru.yandex.practicum.filmorate.util.ValidationUtil.MIN_FILM_RELEASE_DATE_STR;

@Data
@Builder
@AllArgsConstructor
public class FilmDto {
    @NotBlank
    @NotNull
    private final String name;
    @NotNull
    @MinDate(value = MIN_FILM_RELEASE_DATE_STR)
    private final LocalDate releaseDate;
    @Positive
    private final int duration;
    @Size(max = MAX_DESCRIPTION_LENGTH)
    private final String description;
    @EqualsAndHashCode.Exclude
    private final Long id;
    private final Rating mpa;
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private final Set<Long> whoLikedUserIds = new HashSet<>();
    @Builder.Default
    private Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));
    @Builder.Default
    private Set<Director> directors = new HashSet<>();
}
