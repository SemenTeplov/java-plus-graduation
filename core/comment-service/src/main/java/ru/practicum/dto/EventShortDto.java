package main.java.ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import main.dto.CategoryDto;

@Setter
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {

        @NotBlank
        String annotation;

        @NotNull
        CategoryDto category;

        @NotNull
        Long confirmedRequests;

        @NotBlank
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")
        String eventDate;

        @NotNull
        Long id;

        @NotNull
        UserShortDto initiator;

        @NotNull
        Boolean paid;

        @NotBlank
        String title;

        @NotNull
        Long views;
}
