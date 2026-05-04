package main.java.ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewCommentDto(
        @NotBlank
        @Size(max = 5000)
        String text
) {
}
