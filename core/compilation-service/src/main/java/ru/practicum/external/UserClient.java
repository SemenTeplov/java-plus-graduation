package main.java.ru.practicum.external;

import main.dto.UserShortDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/users/client")
    ResponseEntity<List<UserShortDto>> getUsersById(@RequestBody List<Long> ids);
}
