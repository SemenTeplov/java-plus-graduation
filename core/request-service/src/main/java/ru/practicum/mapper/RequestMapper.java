package main.java.ru.practicum.mapper;

import main.dto.ParticipationRequestDto;
import main.java.ru.practicum.constant.Values;
import main.java.ru.practicum.persistence.entity.Request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern(Values.DATE_TIME_PATTERN)
                    .withZone(ZoneId.of("UTC"));

    @Mapping(target = "created", source = "created", qualifiedByName = "toString")
    ParticipationRequestDto toParticipationRequestDto(Request participationRequest);

    @Mapping(target = "created", source = "created", qualifiedByName = "toLocalDateTime")
    Request toRequest(ParticipationRequestDto participationRequest);

    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "event", source = "eventId")
    @Mapping(target = "requester", source = "userId")
    @Mapping(target = "status", source = "status")
    Request toNewRequest(Long userId, Long eventId, String status);

    @Named("toString")
    default String toString(LocalDateTime time) {
        if (time == null) {
            return null;
        }

        return FORMATTER.format(time);
    }

    @Named("toLocalDateTime")
    default LocalDateTime toLocalDateTime(String time) {
        if (time == null) {
            return null;
        }

        return LocalDateTime.parse(time, FORMATTER);
    }
}
