package main.java.ru.practicum.external;

import main.java.ru.practicum.external.config.FeignErrorDecoder;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", configuration = FeignErrorDecoder.class)
public interface UserClient {

    @GetMapping("/users/client/check/{userId}")
    ResponseEntity<Void> checkUser(@PathVariable("userId") Long userId);
}
