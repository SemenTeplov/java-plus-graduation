package main.java.ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RequestCategoryDto(
        @NotBlank
        @Size(min = 1, max = 50)
        String name
) {
}
