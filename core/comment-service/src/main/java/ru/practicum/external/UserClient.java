package main.java.ru.practicum.external;

import main.dto.UserShortDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/users/{userId}")
    ResponseEntity<UserShortDto> getUserById(@PathVariable("userId") Long userId);
}
