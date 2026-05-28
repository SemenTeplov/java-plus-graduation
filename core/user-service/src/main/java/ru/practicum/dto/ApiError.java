package main.java.ru.practicum.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ApiError(
        List<String> errors,
        String message,
        String reason,
        String status,
        String timestamp
) {
}
