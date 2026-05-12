package main.java.ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RequestCommentDto(
        @NotBlank
        @Size(max = 5000)
        String text
) {
}
