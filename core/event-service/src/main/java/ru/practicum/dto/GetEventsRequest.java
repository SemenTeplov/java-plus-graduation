package main.java.ru.practicum.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetEventsRequest {

    String text;

    List<Long> categories;

    Boolean paid;

    String rangeStart;

    String rangeEnd;

    Boolean onlyAvailable;

    String sort;

    Integer from;

    Integer size;
}
