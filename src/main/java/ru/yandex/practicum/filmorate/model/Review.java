package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    @EqualsAndHashCode.Exclude
    private Long id;
    private User user;
    private Film film;
    private String content;
    private Boolean isPositive;
    @Builder.Default
    private int useful = 0;
}
