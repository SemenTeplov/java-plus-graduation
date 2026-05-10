package main.java.ru.practicum.external;

import main.java.ru.practicum.external.config.FeignErrorDecoder;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "event-service", configuration = FeignErrorDecoder.class)
public interface EventClient {

    @GetMapping("/events/category/{categoryId}")
    ResponseEntity<Boolean> existsByCategoryId(@PathVariable("categoryId") Long eventId);
}
