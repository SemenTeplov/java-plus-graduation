package main.java.ru.practicum.controller;

import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import main.java.ru.practicum.dto.EventShortDto;
import main.java.ru.practicum.dto.ResponseEventFullDto;
import main.java.ru.practicum.dto.EventRequestStatusRequest;
import main.java.ru.practicum.dto.EventRequestStatusUpdateResult;
import main.java.ru.practicum.dto.GetEventsForAdminRequest;
import main.java.ru.practicum.dto.GetEventsRequest;
import main.java.ru.practicum.dto.RequestEventDto;
import main.java.ru.practicum.dto.ParticipationRequestDto;
import main.java.ru.practicum.dto.UpdateEventAdminRequest;
import main.java.ru.practicum.dto.UpdateEventUserRequest;
import main.java.ru.practicum.service.EventService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class EventController {

    private final EventService eventService;

    @PostMapping("/users/{userId}/events")
    ResponseEntity<ResponseEventFullDto> addEvent(@PathVariable("userId") Long userId,
                                                  @Valid @RequestBody RequestEventDto request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.addEvent(userId, request));
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    ResponseEntity<EventRequestStatusUpdateResult> changeRequestStatus(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @Valid @RequestBody EventRequestStatusRequest eventRequestStatusRequest) {

        return ResponseEntity.ok(eventService.changeRequestStatus(userId, eventId, eventRequestStatusRequest));
    }

    @GetMapping("/events/{id}")
    ResponseEntity<ResponseEventFullDto> getEvent(@PathVariable("id") Long id,
                                                  @RequestHeader("X-EWM-USER-ID") Long userId) {

        return ResponseEntity.ok(eventService.getEvent(id, userId));
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    ResponseEntity<List<ParticipationRequestDto>> getEventParticipants(@PathVariable("userId") Long userId,
                                                                       @PathVariable("eventId") Long eventId) {

        return ResponseEntity.ok(eventService.getEventParticipants(userId, eventId));
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    ResponseEntity<ResponseEventFullDto> getEventUser(@PathVariable("userId") Long userId, @PathVariable("eventId") Long eventId) {

        return ResponseEntity.ok(eventService.getEventUser(userId, eventId));
    }

    @GetMapping("/events")
    ResponseEntity<List<EventShortDto>> getEvents(@RequestParam(value = "text", required = false) String text,
                                                  @RequestParam(value = "categories", required = false) List<Long> categories,
                                                  @RequestParam(value = "paid", required = false) Boolean paid,
                                                  @RequestParam(value = "rangeStart", required = false) String rangeStart,
                                                  @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
                                                  @RequestParam(value = "onlyAvailable", required = false, defaultValue = "false") Boolean onlyAvailable,
                                                  @RequestParam(value = "sort", required = false) @Nullable String sort,
                                                  @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                                  @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        return ResponseEntity.ok(eventService.getEvents(GetEventsRequest.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .build()));
    }

    @GetMapping("/admin/events")
    ResponseEntity<List<ResponseEventFullDto>> getEventsAdmin(@RequestParam(value = "users", required = false) List<Long> users,
                                                              @RequestParam(value = "states", required = false) List<String> states,
                                                              @RequestParam(value = "categories", required = false) List<Long> categories,
                                                              @RequestParam(value = "rangeStart", required = false) String rangeStart,
                                                              @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
                                                              @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                                              @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        return ResponseEntity.ok(eventService.getEventsAdmin(GetEventsForAdminRequest.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build()));
    }

    @GetMapping("/users/{userId}/events")
    ResponseEntity<List<EventShortDto>> getEventsUser(@PathVariable("userId") Long userId,
                                                      @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                                      @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        return ResponseEntity.ok(eventService.getEventsUser(userId, from, size));
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    ResponseEntity<ResponseEventFullDto> updateEvent(@PathVariable("userId") Long userId,
                                                     @PathVariable("eventId") Long eventId,
                                                     @RequestBody(required = false) UpdateEventUserRequest updateEventUserRequest) {

        return ResponseEntity.ok(eventService.updateEvent(userId, eventId, updateEventUserRequest));
    }

    @PatchMapping("/admin/events/{eventId}")
    ResponseEntity<ResponseEventFullDto> updateEventAdmin(@PathVariable("eventId") Long eventId,
                                                          @RequestBody(required = false) UpdateEventAdminRequest updateEventAdminRequest) {

        return ResponseEntity.ok(eventService.updateEventAdmin(eventId, updateEventAdminRequest));
    }

    @GetMapping("/users/client/{eventId}/{userId}/{status}/{count}")
    ResponseEntity<String> getStatus(@PathVariable("eventId") Long eventId,
                                     @PathVariable("userId") Long userId,
                                     @PathVariable("status") String status,
                                     @PathVariable("count") Long count) {

        return ResponseEntity.ok(eventService.getStatus(eventId, userId, status, count));
    }

    @GetMapping("/events/recommendations")
    ResponseEntity<List<EventShortDto>> getRecommendations(@RequestHeader("X-EWM-USER-ID") Long userId) {

        return ResponseEntity.ok(eventService.getRecommendations(userId));
    }

    @PutMapping("/events/{eventId}/like")
    ResponseEntity<Void> sendLike(@RequestHeader("X-EWM-USER-ID") Long userId,
                                  @RequestParam("eventId") Long eventId) {

        eventService

        return ResponseEntity.ok().build();
    }
}
