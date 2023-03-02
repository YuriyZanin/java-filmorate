package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.film.mapper.DirectorMapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    public static final String DIRECTORS_QUERY = "SELECT * FROM directors\n";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Director> getAll() {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(DIRECTORS_QUERY);
        return DirectorMapper.makeDirectorList(rowSet);
    }

    @Override
    public Optional<Director> get(Long id) {
        String query = DIRECTORS_QUERY + "WHERE id = ?";
        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, id);
            return DirectorMapper.makeDirectorList(rowSet).stream().findAny();
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Director create(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("DIRECTORS")
                .usingGeneratedKeyColumns("ID");
        Long id = simpleJdbcInsert.executeAndReturnKey(directorToParameters(director)).longValue();
        director.setId(id);
        return director;
    }

    @Override
    public Director update(Director director) {
        int updatedRows = jdbcTemplate.update(
                "UPDATE directors SET name = ? WHERE id = ?", director.getName(), director.getId());
        if (updatedRows == 0) {
            return null;
        }
        return director;
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM directors WHERE id = ?", id);
    }

    private Map<String, Object> directorToParameters(Director director) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", director.getId());
        parameters.put("name", director.getName());
        return parameters;
    }
}
