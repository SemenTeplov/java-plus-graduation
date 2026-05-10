package main.java.ru.practicum.external;

import main.java.ru.practicum.dto.EventShortDto;
import main.java.ru.practicum.external.config.FeignErrorDecoder;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "event-service", configuration = FeignErrorDecoder.class)
public interface EventClient {

    @GetMapping("/events/compilations")
    ResponseEntity<List<EventShortDto>> getAllById(@RequestParam List<Long> ids);
}
