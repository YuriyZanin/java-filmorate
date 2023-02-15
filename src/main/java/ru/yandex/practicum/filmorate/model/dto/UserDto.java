package ru.yandex.practicum.filmorate.model.dto;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.util.ValidationUtil;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class UserDto {
    @NotNull
    @NotBlank
    @Email
    private final String email;
    @NotNull
    @Pattern(regexp = ValidationUtil.LOGIN_PATTERN)
    private final String login;
    @PastOrPresent
    private final LocalDate birthday;
    private final Long id;
    private final String name;
    @Builder.Default
    private final Set<Long> friendIds = new HashSet<>();
}
