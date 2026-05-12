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
public class GetEventsForAdminRequest {

    List<Long> users;

    List<String> states;

    List<Long> categories;

    String rangeStart;

    String rangeEnd;

    Integer from;

    Integer size;
}
