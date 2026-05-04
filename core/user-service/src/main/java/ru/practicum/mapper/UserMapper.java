package main.java.ru.practicum.mapper;

import main.dto.UserShortDto;
import main.java.ru.practicum.dto.NewUserRequest;
import main.java.ru.practicum.dto.UserDto;
import main.java.ru.practicum.persistence.entity.User;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);

    User toUser(NewUserRequest newUserRequest);

    UserShortDto userToUserShortDto(User user);
}
