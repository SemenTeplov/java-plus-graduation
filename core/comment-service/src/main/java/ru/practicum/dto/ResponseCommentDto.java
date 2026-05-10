package main.java.ru.practicum.dto;

import main.dto.EventShortDto;
import main.dto.UserShortDto;

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
