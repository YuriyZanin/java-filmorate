package ru.yandex.practicum.filmorate.storage.film.mapper;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.LinkedList;
import java.util.List;

public class DirectorMapper {

    public static List<Director> makeDirectorList(SqlRowSet rs) {
        List<Director> directors = new LinkedList<>();
        while (rs.next()) {
            directors.add(makeDirector(rs));
        }
        return directors;
    }
    public static Director makeDirector(SqlRowSet rs) {
        return Director.builder()
                .id(rs.getLong("ID"))
                .name(rs.getString("NAME"))
                .build();
    }
}
