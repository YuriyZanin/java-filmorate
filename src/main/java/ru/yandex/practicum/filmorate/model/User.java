package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.util.ValidationUtil;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    @NotNull
    @NotBlank
    @Email
    private final String email;
    @NotNull
    @Pattern(regexp = ValidationUtil.LOGIN_PATTERN)
    private final String login;
    @PastOrPresent
    private final LocalDate birthday;
    private Long id;
    private String name;
    private Set<Long> friendIds = new HashSet<>();
}
