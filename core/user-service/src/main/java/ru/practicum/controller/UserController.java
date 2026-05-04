package main.java.ru.practicum.controller;

import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import main.dto.UserShortDto;
import main.java.ru.practicum.dto.GetUsersRequest;
import main.java.ru.practicum.dto.NewUserRequest;
import main.java.ru.practicum.dto.UserDto;
import main.java.ru.practicum.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class UserController {

    private final UserService userService;

    @DeleteMapping("/admin/users/{userId}")
    ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {

        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/users")
    ResponseEntity<List<UserDto>> getUsers(@RequestParam(value = "ids", required = false) List<Long> ids,
                                           @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                           @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        GetUsersRequest request = new GetUsersRequest(ids, from, size);

        return ResponseEntity.ok(userService.getUsers(request));
    }

    @PostMapping("/admin/users")
    ResponseEntity<UserDto> registerUser(@Valid @RequestBody NewUserRequest newUserRequest) {

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(newUserRequest));
    }

    @GetMapping("/users/{userId}")
    ResponseEntity<UserShortDto> getUserById(@PathVariable("userId") Long userId) {

        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/users/client")
    ResponseEntity<List<UserShortDto>> getUsersById(@RequestBody List<Long> ids) {

        return ResponseEntity.ok(userService.getUsersById(ids));
    }

    @GetMapping("/users/client/check/{userId}")
    ResponseEntity<Void> checkUser(@PathVariable("userId") Long userId) {

        return ResponseEntity.ok(userService.checkUser(userId));
    }
}
