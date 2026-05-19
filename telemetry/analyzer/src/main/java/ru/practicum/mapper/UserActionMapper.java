package main.java.ru.practicum.mapper;

import main.java.ru.practicum.persistence.model.UserAction;
import main.java.ru.practicum.persistence.model.UserActionId;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import ru.practicum.ewm.stats.avro.UserActionAvro;

import stats.messages.RecommendedEventProto;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserActionMapper {

    @Mapping(target = "userActionId", source = "user", qualifiedByName = "toUserActionId")
    @Mapping(target = "timestampMs", expression = "java(java.time.LocalDateTime.ofInstant(user.getTimestamp(), java.time.ZoneId.of(\"Europe/Moscow\")))")
    UserAction toUserAction(UserActionAvro user);

    @Mapping(target = "mergeFrom", ignore = true)
    @Mapping(target = "clearField", ignore = true)
    @Mapping(target = "clearOneof", ignore = true)
    @Mapping(target = "unknownFields", ignore = true)
    @Mapping(target = "mergeUnknownFields", ignore = true)
    @Mapping(target = "allFields", ignore = true)
    @Mapping(target = "eventId", expression = "java(java.lang.Math.toIntExact(user.getUserActionId().getEventId()))")
    @Mapping(target = "score", expression = "java(main.java.ru.practicum.persistence.status.ActionType.getValue(user.getActionType().name()))")
    RecommendedEventProto toRecommendedEventProto(UserAction user);

    @Named("toUserActionId")
    default UserActionId toUserActionId(UserActionAvro user) {

        if (user.getTimestamp() == null) {
            return null;
        }

        return new UserActionId((long) user.getUserId(), (long) user.getEventId());
    }
}