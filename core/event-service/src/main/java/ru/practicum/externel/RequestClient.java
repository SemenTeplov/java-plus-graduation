package main.java.ru.practicum.externel;

import main.dto.ParticipationRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "request-service")
public interface RequestClient {

    @GetMapping("/request/client")
    ResponseEntity<List<ParticipationRequestDto>> getRequestsByIds(@RequestParam List<Long> ids);

    @GetMapping("/request/{eventId}/{status}")
    ResponseEntity<Long> countByEventIdAndStatus(@PathVariable("eventId") Long eventId,
                                                 @PathVariable("status") String status);

    @PostMapping("/users/requests")
    ResponseEntity<List<ParticipationRequestDto>> addParticipationRequests(@RequestBody List<ParticipationRequestDto> list);

    @GetMapping("/request/client/{eventId}")
    ResponseEntity<List<ParticipationRequestDto>> getRequestsByEvent(@PathVariable("eventId") Long eventId);
}
