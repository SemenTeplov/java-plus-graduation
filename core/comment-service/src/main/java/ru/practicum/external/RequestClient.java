package main.java.ru.practicum.external;

import main.java.ru.practicum.dto.ParticipationRequestDto;
import main.java.ru.practicum.external.config.FeignErrorDecoder;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "request-service", configuration = FeignErrorDecoder.class)
public interface RequestClient {

    @GetMapping("/request/{eventId}/{status}")
    ResponseEntity<Long> countByEventIdAndStatus(@PathVariable("eventId") Long eventId,
                                                 @PathVariable("status") String status);

    @GetMapping("/request")
    ResponseEntity<List<ParticipationRequestDto>> getAll();
}
