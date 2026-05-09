package main.java.ru.practicum.mapper;

import main.dto.EventShortDto;
import main.java.ru.practicum.constant.Values;
import main.java.ru.practicum.dto.EventFullDto;
import main.java.ru.practicum.dto.NewEventDto;
import main.java.ru.practicum.dto.UpdateEventAdminRequest;
import main.java.ru.practicum.dto.UpdateEventUserRequest;
import main.java.ru.practicum.persistence.entity.Event;
import main.java.ru.practicum.persistence.status.State;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {
    static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern(Values.DATE_TIME_PATTERN);

    @Mapping(target = "eventDate", qualifiedByName = "toOffsetDateTime")
    @Mapping(target = "location", ignore = true)
    Event newEventDtoToEvent(NewEventDto newEventDto);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "state", qualifiedByName = "toStatusEnum")
    @Mapping(target = "eventDate", qualifiedByName = "toStringFromTime")
    EventFullDto eventToEventFullDto(Event event);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "eventDate", qualifiedByName = "toStringFromTime")
    EventShortDto eventToEventShortDto(Event event);

    @Mapping(target = "eventDate", qualifiedByName = "toOffsetDateTime")
    @Mapping(target = "location", source = "locationId")
    void updateEventUserRequestToEvent(@MappingTarget Event event, Long locationId,
                                        UpdateEventUserRequest updateEventUserRequest);

    @Mapping(target = "eventDate", qualifiedByName = "toOffsetDateTime")
    @Mapping(target = "location", source = "locationId")
    @Mapping(target = "title", qualifiedByName = "checkTitleSize")
    void updateEventAdminRequestToEvent(@MappingTarget Event event, Long locationId,
                                         UpdateEventAdminRequest updateEventAdminRequest);

    @Named("toOffsetDateTime")
    default OffsetDateTime toOffsetDateTime(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }

        return ZonedDateTime.of(
                LocalDateTime.parse(str, FORMATTER),
                ZoneId.of("UTC")).toOffsetDateTime();
    }

    @Named("toStringFromTime")
    default String toStringFromTime(OffsetDateTime date) {

        return date.atZoneSameInstant(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern(Values.DATE_TIME_PATTERN));
    }

    @Named("toStatusEnum")
    default State toStatusEnum(String status) {
        return State.valueOf(status);
    }

    @Named("checkTitleSize")
    default String checkTitleSize(String title) {
        if (title.length() < 3 || title.length() > 120) {
            throw new IllegalArgumentException();
        }

        return title;
    }
}
