package main.java.ru.practicum.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Builder;

@Builder
public record RequestEventDto(
        @NotBlank
        @Size(min = 20, max = 2000)
        String annotation,

        Long category,

        @NotBlank
        @Size(min = 20, max = 7000)
        String description,

        @NotBlank
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")
        String eventDate,

        LocationDto location,

        Boolean paid,

        @Min(0)
        Integer participantLimit,

        Boolean requestModeration,

        @NotBlank
        @Size(min = 3, max = 120)
        String title
) {
}
