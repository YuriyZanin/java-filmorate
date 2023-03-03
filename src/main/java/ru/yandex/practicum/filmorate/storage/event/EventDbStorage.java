package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.event.mapper.EventMapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    public EventDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Event create(Event event) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("events")
                .usingGeneratedKeyColumns("id");
        Long eventId = simpleJdbcInsert.executeAndReturnKey(eventToParameters(event)).longValue();
        event.setId(eventId);
        return event;
    }

    @Override
    public Collection<Event> getFeedList(Long userId) {
        String query =
                "        SELECT e.id                   AS event_id,\n" +
                        "       e.type,\n" +
                        "       e.operation,\n" +
                        "       e.entity_id,\n" +
                        "       e.event_time,\n" +
                        "       u.id                   AS user_id,\n" +
                        "       u.name                 AS user_name,\n" +
                        "       u.birthday,\n" +
                        "       u.login,\n" +
                        "       u.email,\n" +
                        "       array_agg(f.friend_id) AS friends_ids\n" +
                        "FROM events AS e \n" +
                        "INNER JOIN users u ON u.id = e.user_id\n" +
                        "LEFT JOIN friendships f ON f.user_id = e.user_id\n" +
                        "WHERE e.user_id = ?\n" +
                        "OR e.user_id IN\n" +
                        "(SELECT fr.friend_id FROM friendships fr WHERE fr.user_id = ?) AND e.entity_id = ?\n" +
                        "GROUP BY e.id, e.event_time\n" +
                        "ORDER BY e.event_time";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, userId, userId, userId);
        return EventMapper.makeEventList(rowSet);
    }

    private Map<String, Object> eventToParameters(Event event) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", event.getId());
        parameters.put("user_id", event.getUser().getId());
        parameters.put("entity_id", event.getEntity().getId());
        parameters.put("type", event.getType());
        parameters.put("operation", event.getOperation());
        parameters.put("event_time", event.getEventTime());
        return parameters;
    }
}
