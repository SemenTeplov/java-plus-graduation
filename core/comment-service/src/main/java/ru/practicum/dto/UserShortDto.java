package main.java.ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;

public record UserShortDto(
        Long id,

        @NotBlank
        String name
) {
}
