package main.java.ru.practicum.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {

        String annotation;

        CategoryDto category;

        Long confirmedRequests;

        String eventDate;

        Long id;

        UserShortDto initiator;

        Boolean paid;

        String title;

        Long views;
}
