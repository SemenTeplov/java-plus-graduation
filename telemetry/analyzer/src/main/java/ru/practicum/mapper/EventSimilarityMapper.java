package main.java.ru.practicum.mapper;

import main.java.ru.practicum.persistence.model.EventSimilarity;
import main.java.ru.practicum.persistence.model.EventSimilarityId;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventSimilarityMapper {

    @Mapping(target = "timestampMs", expression = "java(java.time.LocalDateTime.ofInstant(event.getTimestamp(), java.time.ZoneId.of(\"Europe/Moscow\")))")
    @Mapping(target = "eventSimilarityId", source = "event", qualifiedByName = "toEventSimilarityId")
    EventSimilarity toEventSimilarity(EventSimilarityAvro event);

    @Named("toEventSimilarityId")
    default EventSimilarityId toEventSimilarityId(EventSimilarityAvro event) {

        if (event == null) {
            return null;
        }

        return new EventSimilarityId((long) event.getEventA(), (long) event.getEventB());
    }
}
