package main.java.ru.practicum.mapper;

import main.dto.ParticipationRequestDto;
import main.java.ru.practicum.constant.Values;
import main.java.ru.practicum.persistence.entity.Request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern(Values.DATE_TIME_PATTERN)
                    .withZone(ZoneId.of("UTC"));

    @Mapping(target = "created", source = "created", qualifiedByName = "toString")
    ParticipationRequestDto toParticipationRequestDto(Request participationRequest);

    @Mapping(target = "created", source = "created", qualifiedByName = "toOffsetDateTime")
    Request toRequest(ParticipationRequestDto participationRequest);

    @Mapping(target = "created", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "event", source = "eventId")
    @Mapping(target = "requester", source = "userId")
    @Mapping(target = "status", source = "status")
    Request toNewRequest(Long userId, Long eventId, String status);

    @Named("toString")
    default String toString(OffsetDateTime time) {
        if (time == null) {
            return null;
        }

        ZonedDateTime utcTime = time.atZoneSameInstant(ZoneId.of("UTC"));
        return FORMATTER.format(utcTime);
    }

    @Named("toOffsetDateTime")
    default OffsetDateTime toOffsetDateTime(String time) {
        if (time == null) {
            return null;
        }

        return OffsetDateTime.parse(time, FORMATTER);
    }
}
