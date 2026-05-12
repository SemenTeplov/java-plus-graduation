package main.java.ru.practicum.mapper;

import main.java.ru.practicum.dto.UserShortDto;
import main.java.ru.practicum.dto.NewUserRequest;
import main.java.ru.practicum.dto.UserDto;
import main.java.ru.practicum.persistence.entity.User;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserDto toUserDto(User user);

    @Mapping(target = "id", ignore = true)
    User toUser(NewUserRequest newUserRequest);

    UserShortDto userToUserShortDto(User user);
}
