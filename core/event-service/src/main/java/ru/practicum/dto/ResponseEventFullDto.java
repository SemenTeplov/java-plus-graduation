package main.java.ru.practicum.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import main.java.ru.practicum.persistence.status.State;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseEventFullDto {

        String annotation;

        CategoryDto category;

        Long confirmedRequests;

        String createdOn;

        String description;

        String eventDate;

        Long id;

        UserShortDto initiator;

        LocationDto location;

        Boolean paid;

        Integer participantLimit;

        String publishedOn;

        Boolean requestModeration;

        State state;

        String title;

        Long views;
}
