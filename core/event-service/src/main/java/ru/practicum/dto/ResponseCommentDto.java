package main.java.ru.practicum.dto;

import lombok.Builder;

@Builder
public record ResponseCommentDto(

        Long id,

        String text,

        UserShortDto author,

        EventShortDto event,

        String created,

        String edited
) {
}
