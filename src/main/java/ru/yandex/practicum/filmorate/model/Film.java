package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@SuperBuilder
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Film extends BaseEntity {
    private String name;
    private LocalDate releaseDate;
    private int duration;
    private String description;
    private Rating mpa;
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Long> whoLikedUserIds = new HashSet<>();
    @Builder.Default
    private Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));
    @Builder.Default
    private Set<Director> directors = new HashSet<>();
}
