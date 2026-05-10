package main.java.ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import main.java.ru.practicum.dto.UserShortDto;
import main.java.ru.practicum.constant.Exceptions;
import main.java.ru.practicum.constant.Messages;
import main.java.ru.practicum.dto.GetUsersRequest;
import main.java.ru.practicum.dto.NewUserRequest;
import main.java.ru.practicum.dto.UserDto;
import main.java.ru.practicum.exception.NotFoundException;
import main.java.ru.practicum.mapper.UserMapper;
import main.java.ru.practicum.persistence.entity.User;
import main.java.ru.practicum.persistence.repository.UserRepository;
import main.java.ru.practicum.exception.ForbiddenException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public UserDto addUser(NewUserRequest newUserRequest) {

        log.info(Messages.MESSAGE_REGISTER_USER, newUserRequest);

        if (userRepository.existsByEmail(newUserRequest.email())) {
            throw new ForbiddenException(String.format(Exceptions.EXCEPTION_CONFLICT_EMAIL, newUserRequest.email()));
        }

        return userMapper.toUserDto(userRepository.save(userMapper.toUser(newUserRequest)));
    }

    @Override
    public List<UserDto> getUsers(GetUsersRequest request) {

        log.info(Messages.MESSAGE_GET_USERS, request.getIds(), request.getFrom(), request.getSize());

        Pageable pageable = PageRequest.of(request.getFrom() / request.getSize(), request.getSize());

        List<User> users;

        if (request.getIds() != null && !request.getIds().isEmpty()) {
            users = userRepository.findAllByIdIn(request.getIds(), pageable);
        } else {
            users = userRepository.findAll(pageable).getContent();
        }

        return users.stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {

        log.info(Messages.MESSAGE_DELETE_USER, userId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format(Exceptions.EXCEPTION_NOT_FOUND_USER, userId));
        }

        userRepository.deleteById(userId);
    }

    @Override
    public UserShortDto getUserById(Long userId) {

        log.info(Messages.MESSAGE_GET_USER_BY_ID, userId);

        return userMapper.userToUserShortDto(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(Exceptions.EXCEPTION_NOT_FOUND_USER, userId))));
    }

    @Override
    public List<UserShortDto> getUsersById(List<Long> ids) {

        log.info(Messages.MESSAGE_GET_USERS_BY_ID, ids);

        return userRepository.getInitiatorByCompilationIds(ids.toArray(Long[]::new))
                .stream().map(userMapper::userToUserShortDto).toList();
    }

    @Override
    public void checkUser(Long userId) {

        log.info(Messages.MESSAGE_CHECK_USER, userId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format(Exceptions.EXCEPTION_NOT_FOUND_USER, userId));
        }
    }
}
