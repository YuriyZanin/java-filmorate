package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    private String name;
    private LocalDate releaseDate;
    private int duration;
    private String description;
    private Long id;
    private Rating mpa;
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Long> whoLikedUserIds = new HashSet<>();
    @Builder.Default
    private Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));
}
