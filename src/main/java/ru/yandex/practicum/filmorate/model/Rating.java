package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Rating extends BaseEntity {
    private String name;

    public Rating(Long id, String name) {
        super(id);
        this.name = name;
    }
}
