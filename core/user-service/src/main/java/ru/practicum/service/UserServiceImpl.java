package main.java.ru.practicum.service;

import jakarta.validation.ValidationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import main.dto.UserShortDto;
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

        validateEmail(newUserRequest.email());
        validateName(newUserRequest.name());

        if (userRepository.existsByEmail(newUserRequest.email())) {
            throw new ForbiddenException(String.format(Exceptions.EXCEPTION_CONFLICT_EMAIL, newUserRequest.email()));
        }

        return userMapper.toUserDto(userRepository.save(userMapper.toUser(newUserRequest)));
    }

    @Override
    public List<UserDto> getUsers(GetUsersRequest request) {

        log.info(Messages.MESSAGE_GET_USERS, request.getIds(), request.getFrom(), request.getSize());

        Pageable pageable = PageRequest.of(request.getFrom() / request.getSize(), request.getSize());

        if (request.getIds() != null && !request.getIds().isEmpty()) {
            List<User> users = userRepository.findAllByIdIn(request.getIds(), pageable);
            return users.stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            List<User> users = userRepository.findAll(pageable).getContent();
            return users.stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        }
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

        return userMapper.userToUserShortDto(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(Exceptions.EXCEPTION_NOT_FOUND_USER, userId))));
    }

    @Override
    public List<UserShortDto> getUsersById(List<Long> ids) {
        return userRepository.getInitiatorByCompilationIds(ids.toArray(Long[]::new))
                .stream().map(userMapper::userToUserShortDto).toList();
    }

    @Override
    public Void checkUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format(Exceptions.EXCEPTION_NOT_FOUND_USER, userId));
        }

        return null;
    }

    private void validateEmail(String email) {
        if (email == null) {
            throw new ValidationException("Email обязателен для заполнения");
        }
        String trimmed = email.trim();
        if (email.isEmpty()) {
            throw new ValidationException("Email не может быть пустым или состоять только из пробелов");
        }
        if (email.length() > 254) {
            throw new ValidationException("Email не может превышать 254 символа");
        }
        String[] parts = trimmed.split("@");
        if (parts.length != 2) {
            throw new ValidationException("Некорректный формат email");
        }
        String localPart = parts[0];
        String domainPart = parts[1];
        if (localPart.length() > 64) {
            throw new ValidationException("Локальная часть email не может превышать 64 символа");
        }
        if (domainPart.length() > 253) {
            throw new ValidationException("Доменная часть email не может превышать 253 символа");
        }
        String[] domainLabels = domainPart.split("\\.");
        for (String label : domainLabels) {
            if (label.length() > 63) {
                throw new ValidationException("Каждая часть домена не может превышать 63 символа");
            }
            if (label.startsWith("-") || label.endsWith("-")) {
                throw new ValidationException("Части домена не могут начинаться или заканчиваться дефисом");
            }
        }
    }

    private void validateName(String name) {
        if (name == null) {
            throw new ValidationException("Имя обязательно для заполнения");
        }
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            throw new ValidationException("Имя не может быть пустым или состоять только из пробелов");
        }
        if (trimmed.length() < 2) {
            throw new ValidationException("Имя должно содержать минимум 2 символа");
        }
        if (trimmed.length() > 250) {
            throw new ValidationException("Имя должно содержать не более 250 символов");
        }
    }
}
