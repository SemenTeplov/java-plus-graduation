package main.java.ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

import main.dto.EventShortDto;
import main.dto.UserShortDto;

import lombok.Builder;

@Builder
public record ResponseCommentDto(
        Long id,

        @NotBlank
        @Size(max = 5000)
        String text,

        UserShortDto author,

        EventShortDto event,

        @NotBlank
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")
        String created,

        @NotBlank
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")
        String edited
) {
}
