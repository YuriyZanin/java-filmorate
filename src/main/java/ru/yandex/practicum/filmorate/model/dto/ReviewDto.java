package ru.yandex.practicum.filmorate.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ReviewDto {
    @EqualsAndHashCode.Exclude
    private final Long reviewId;
    @NotNull
    private final Long userId;
    @NotNull
    private final Long filmId;
    @NotBlank(message = "Поле \"Содержание\" должно быть заполнено")
    private final String content;
    @NotNull
    private final Boolean isPositive;
    @Builder.Default
    private final int useful = 0;
}
