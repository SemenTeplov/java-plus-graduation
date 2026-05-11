package main.java.ru.practicum.dto;

import jakarta.validation.constraints.Size;

public record ResponseCategoryDto(

        Long id,

        @Size(min = 1, max = 50)
        String name
) {
}
