package main.java.ru.practicum.externel;

import main.dto.UserShortDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/users/{userId}")
    ResponseEntity<UserShortDto> getUserById(@PathVariable("userId") Long userId);

    @GetMapping("/users/client")
    ResponseEntity<List<UserShortDto>> getUsersById(@RequestParam List<Long> ids);
}
