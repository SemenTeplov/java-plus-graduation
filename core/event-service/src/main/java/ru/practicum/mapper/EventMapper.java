package main.java.ru.practicum.mapper;

import main.java.ru.practicum.dto.EventShortDto;
import main.java.ru.practicum.constant.Values;
import main.java.ru.practicum.dto.ResponseEventFullDto;
import main.java.ru.practicum.dto.RequestEventDto;
import main.java.ru.practicum.dto.UpdateEventAdminRequest;
import main.java.ru.practicum.dto.UpdateEventUserRequest;
import main.java.ru.practicum.persistence.entity.Event;
import main.java.ru.practicum.persistence.status.State;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {

    DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern(Values.DATE_TIME_PATTERN);

    @Mapping(target = "eventDate", qualifiedByName = "toOffsetDateTime")
    @Mapping(target = "location", source = "locationId")
    @Mapping(target = "initiator", source = "userId")
    @Mapping(target = "state", expression = "java(main.java.ru.practicum.persistence.status.State.PENDING)")
    @Mapping(target = "confirmedRequests", constant = "0")
    @Mapping(target = "views", constant = "0L")
    @Mapping(target = "paid", expression = "java(newEventDto.paid() == null ? false : newEventDto.paid())")
    @Mapping(target = "participantLimit",
            expression = "java(newEventDto.participantLimit() == null ? 0 : newEventDto.participantLimit())")
    @Mapping(target = "requestModeration",
            expression = "java(newEventDto.requestModeration() == null ? true : newEventDto.requestModeration())")
    Event newEventDtoToEvent(RequestEventDto newEventDto, Long userId, Long locationId);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", qualifiedByName = "toStatusEnum")
    @Mapping(target = "eventDate", qualifiedByName = "toStringFromTime")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now().toString())")
    ResponseEventFullDto eventToEventFullDto(Event event);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "eventDate", qualifiedByName = "toStringFromTime")
    EventShortDto eventToEventShortDto(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "eventDate", qualifiedByName = "toOffsetDateTime")
    @Mapping(target = "location", source = "locationId")
    @Mapping(target = "title", qualifiedByName = "checkTitleSize")
    @Mapping(target = "annotation", qualifiedByName = "checkAnnotationSize")
    @Mapping(target = "description", qualifiedByName = "checkDescriptionSize")
    @Mapping(target = "participantLimit", qualifiedByName = "checkParticipantLimitSize")
    void updateEventUserRequestToEvent(@MappingTarget Event event, Long locationId,
                                        UpdateEventUserRequest updateEventUserRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "eventDate", qualifiedByName = "toOffsetDateTime")
    @Mapping(target = "location", source = "locationId")
    @Mapping(target = "title", qualifiedByName = "checkTitleSize")
    @Mapping(target = "annotation", qualifiedByName = "checkAnnotationSize")
    @Mapping(target = "description", qualifiedByName = "checkDescriptionSize")
    @Mapping(target = "participantLimit", qualifiedByName = "checkParticipantLimitSize")
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
        if (date == null) {
            return null;
        }

        return date.atZoneSameInstant(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern(Values.DATE_TIME_PATTERN));
    }

    @Named("toStatusEnum")
    default State toStatusEnum(String status) {
        if (status == null || status.isEmpty()) {
            return null;
        }

        return State.valueOf(status);
    }

    @Named("checkTitleSize")
    default String checkTitleSize(String title) {
        if (title == null || title.isEmpty()) {
            return null;
        }

        if (title.length() < 3 || title.length() > 120) {
            throw new IllegalArgumentException();
        }

        return title;
    }

    @Named("checkAnnotationSize")
    default String checkAnnotationSize(String annotation) {
        if (annotation == null || annotation.isEmpty()) {
            return null;
        }

        if (annotation.length() < 20 || annotation.length() > 2000) {
            throw new IllegalArgumentException();
        }

        return annotation;
    }

    @Named("checkDescriptionSize")
    default String checkDescriptionSize(String description) {
        if (description == null || description.isEmpty()) {
            return null;
        }

        if (description.length() < 20 || description.length() > 7000) {
            throw new IllegalArgumentException();
        }

        return description;
    }

    @Named("checkParticipantLimitSize")
    default Integer checkParticipantLimitSize(Integer participantLimit) {
        if (participantLimit == null) {
            return null;
        }

        if (participantLimit < 0) {
            throw new IllegalArgumentException();
        }

        return participantLimit;
    }
}
