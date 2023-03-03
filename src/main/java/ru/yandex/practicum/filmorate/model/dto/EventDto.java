package ru.yandex.practicum.filmorate.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EventDto {
    private final Long eventId;
    private final Long userId;
    private final String eventType;
    private final String operation;
    private final Long entityId;
    private final Long timestamp;
}
