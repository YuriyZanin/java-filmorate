package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.util.validation.MinDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static ru.yandex.practicum.filmorate.util.ValidationUtil.MAX_DESCRIPTION_LENGTH;
import static ru.yandex.practicum.filmorate.util.ValidationUtil.MIN_FILM_RELEASE_DATE_STR;

@Data
public class Film {
    @NotBlank
    @NotNull
    private final String name;
    @NotNull
    @MinDate(value = MIN_FILM_RELEASE_DATE_STR)
    private final LocalDate releaseDate;
    @Positive
    private final int duration;
    @Size(max = MAX_DESCRIPTION_LENGTH)
    private String description;
    private Long id;
    private final Set<Long> whoLikedUserIds = new HashSet<>();
}
