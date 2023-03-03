package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Review extends BaseEntity {
    private User user;
    private Film film;
    private String content;
    private Boolean isPositive;
    @Builder.Default
    private int useful = 0;
}
