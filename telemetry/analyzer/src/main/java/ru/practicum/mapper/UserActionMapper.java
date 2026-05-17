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

import java.time.LocalDateTime;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserActionMapper {

    @Mapping(target = "userActionId", source = "user", qualifiedByName = "toUserActionId")
    @Mapping(target = "timestampMs", expression = "java(java.time.LocalDateTime.ofInstant(user.getTimestamp()))")
    UserAction toUserAction(UserActionAvro user);

    @Named("toUserActionId")
    default UserActionId toUserActionId(UserActionAvro user) {

        if (user == null) {
            return null;
        }

        return new UserActionId((long) user.getUserId(), (long) user.getEventId());
    }
}