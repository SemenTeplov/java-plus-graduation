package main.java.ru.practicum.dto;

import jakarta.validation.constraints.Pattern;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import main.java.ru.practicum.dto.LocationDto;

import main.java.ru.practicum.persistence.status.StateAction;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventUserRequest {

    String annotation;

    Long category;

    String description;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")
    String eventDate;

    LocationDto location;

    Boolean paid;

    Integer participantLimit;

    Boolean requestModeration;

    StateAction stateAction;

    String title;
}
