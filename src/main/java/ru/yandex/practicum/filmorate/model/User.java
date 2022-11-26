package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
public class User {
    @NotNull
    @NotBlank
    @Email
    private final String email;
    @NotNull
    @NotBlank
    private final String login;
    @PastOrPresent
    private final LocalDate birthday;
    private Integer id;
    private String name;
}
