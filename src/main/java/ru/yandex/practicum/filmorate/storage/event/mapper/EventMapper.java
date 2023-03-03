package ru.yandex.practicum.filmorate.storage.event.mapper;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.dto.EventDto;
import ru.yandex.practicum.filmorate.storage.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;

public class EventMapper {
    public static List<Event> makeEventList(SqlRowSet rs) {
        List<Event> events = new LinkedList<>();
        while (rs.next()) {
            events.add(makeEvent(rs));
        }
        return events;
    }

    public static Event makeEvent(SqlRowSet rs) {
        return Event.builder()
                .id(rs.getLong("EVENT_ID"))
                .user(UserMapper.makeUser(rs))
                .type(EventType.valueOf(rs.getString("TYPE")))
                .operation(Operation.valueOf(rs.getString("OPERATION")))
                .entity(BaseEntity.builder().id(rs.getLong("ENTITY_ID")).build())
                .eventTime(rs.getTimestamp("EVENT_TIME").toLocalDateTime())
                .build();
    }

    public static EventDto toEventDto(Event event) {
        return EventDto.builder()
                .eventId(event.getId())
                .userId(event.getUser().getId())
                .eventType(event.getType().name())
                .operation(event.getOperation().name())
                .entityId(event.getEntity().getId())
                .timestamp(event.getEventTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .build();
    }

    public static Event toEvent(User user, BaseEntity entity, EventType type, Operation operation, LocalDateTime time) {
        return Event.builder()
                .user(user)
                .entity(entity)
                .type(type)
                .operation(operation)
                .eventTime(time)
                .build();
    }
}
