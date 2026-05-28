package main.java.ru.practicum.service;

import main.java.ru.practicum.dto.EventShortDto;
import main.java.ru.practicum.dto.ResponseEventFullDto;
import main.java.ru.practicum.dto.EventRequestStatusRequest;
import main.java.ru.practicum.dto.GetEventsForAdminRequest;
import main.java.ru.practicum.dto.GetEventsRequest;
import main.java.ru.practicum.dto.RequestEventDto;
import main.java.ru.practicum.dto.EventRequestStatusUpdateResult;
import main.java.ru.practicum.dto.ParticipationRequestDto;
import main.java.ru.practicum.dto.UpdateEventAdminRequest;
import main.java.ru.practicum.dto.UpdateEventUserRequest;

import java.util.List;

public interface EventService {

    ResponseEventFullDto addEvent(Long userId, RequestEventDto newEventDto);

    EventRequestStatusUpdateResult changeRequestStatus(Long userId,
                                                                       Long eventId,
                                                                       EventRequestStatusRequest eventRequestStatusRequest);

    ResponseEventFullDto getEvent(Long id, Long userId);

    List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId);

    ResponseEventFullDto getEventUser(Long userId, Long eventId);

    List<EventShortDto> getEvents(GetEventsRequest request);

    List<ResponseEventFullDto> getEventsAdmin(GetEventsForAdminRequest request);

    List<EventShortDto> getEventsUser(Long userId, Integer from, Integer size);

    ResponseEventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    ResponseEventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    String getStatus(Long eventId, Long userId, String status, Long count);

    List<EventShortDto> getRecommendations(Long userId);

    void sendLike(Long userId, Long eventId);
}
