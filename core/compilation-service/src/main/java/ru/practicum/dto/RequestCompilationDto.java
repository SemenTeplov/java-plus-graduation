package main.java.ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RequestCompilationDto(

        List<Long> events,

        Boolean pinned,

        @NotBlank
        @Size(min = 1, max = 50)
        String title
) {
}
