package main.java.ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import main.dto.CategoryDto;
import main.dto.LocationDto;
import main.dto.UserShortDto;

import main.java.ru.practicum.persistence.status.State;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {

        @NotBlank
        String annotation;

        CategoryDto category;

        Long confirmedRequests;

        @NotBlank
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")
        String createdOn;

        @NotBlank
        String description;

        @NotBlank
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")
        String eventDate;

        Long id;

        UserShortDto initiator;

        LocationDto location;

        Boolean paid;

        Integer participantLimit;

        @NotBlank
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")
        String publishedOn;

        Boolean requestModeration;

        State state;

        @NotBlank
        String title;

        Long views;
}
