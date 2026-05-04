package main.java.ru.practicum.service;

import main.dto.UserShortDto;
import main.java.ru.practicum.dto.GetUsersRequest;
import main.java.ru.practicum.dto.NewUserRequest;
import main.java.ru.practicum.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto addUser(NewUserRequest newUserRequest);

    List<UserDto> getUsers(GetUsersRequest request);

    void deleteUser(Long userId);

    UserShortDto getUserById(Long userId);

    List<UserShortDto> getUsersById(List<Long> ids);

    Void checkUser(Long userId);
}
