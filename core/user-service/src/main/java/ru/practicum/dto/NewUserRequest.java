package main.java.ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewUserRequest(
        @NotBlank
        @Size(min = 6, max = 254)
        String email,

        @NotBlank
        @Size(min = 2, max = 250)
        String name
) {
}
