package main.java.ru.practicum.controller;

import jakarta.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import main.java.ru.practicum.dto.ParticipationRequestDto;
import main.java.ru.practicum.service.RequestService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class RequestController {

    private final RequestService requestService;

    @PostMapping("/users/{userId}/requests")
    ResponseEntity<ParticipationRequestDto> addParticipationRequest(@NotNull @PathVariable("userId") Long userId,
                                                                    @NotNull @RequestParam(value = "eventId") Long eventId) {

        return ResponseEntity.status(HttpStatus.CREATED).body(requestService.addRequest(userId, eventId));
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    ResponseEntity<ParticipationRequestDto> cancelRequest(@NotNull @PathVariable("userId") Long userId,
                                                          @NotNull @PathVariable("requestId") Long requestId) {

        return ResponseEntity.ok(requestService.cancelRequest(userId, requestId));
    }

    @GetMapping("/users/{userId}/requests")
    ResponseEntity<List<ParticipationRequestDto>> getUserRequests(@NotNull @PathVariable("userId") Long userId) {

        return ResponseEntity.ok(requestService.getRequestsByUser(userId));
    }

    @GetMapping("/request/{eventId}/{status}")
    ResponseEntity<Long> countByEventIdAndStatus(@NotNull @PathVariable("eventId") Long eventId,
                                                 @NotNull @PathVariable("status") String status) {

        return ResponseEntity.ok(requestService.countByEventIdAndStatus(eventId, status));
    }

    @GetMapping("/request")
    ResponseEntity<List<ParticipationRequestDto>> getAll() {

        return ResponseEntity.ok(requestService.getAll());
    }

    @GetMapping("/request/client")
    ResponseEntity<List<ParticipationRequestDto>> getRequestsByIds(@RequestParam List<Long> ids) {

        return ResponseEntity.ok(requestService.getRequestsByIds(ids));
    }

    @PostMapping("/users/requests")
    ResponseEntity<List<ParticipationRequestDto>> addParticipationRequests(@RequestBody List<ParticipationRequestDto> list) {

        return ResponseEntity.status(HttpStatus.CREATED).body(requestService.addParticipationRequests(list));
    }

    @GetMapping("/request/client/{eventId}")
    ResponseEntity<List<ParticipationRequestDto>> getRequestsByEvent(@NotNull @PathVariable("eventId") Long eventId) {

        return ResponseEntity.ok(requestService.getRequestsByEvent(eventId));
    }
}
