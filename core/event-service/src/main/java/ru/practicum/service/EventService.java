package main.java.ru.practicum.service;

import main.dto.EventShortDto;
import main.java.ru.practicum.dto.EventFullDto;
import main.dto.EventRequestStatusRequest;
import main.java.ru.practicum.dto.GetEventsForAdminRequest;
import main.java.ru.practicum.dto.GetEventsRequest;
import main.java.ru.practicum.dto.NewEventDto;
import main.dto.EventRequestStatusUpdateResult;
import main.dto.ParticipationRequestDto;
import main.java.ru.practicum.dto.UpdateEventAdminRequest;
import main.java.ru.practicum.dto.UpdateEventUserRequest;

import java.util.List;

public interface EventService {

    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    EventRequestStatusUpdateResult changeRequestStatus(Long userId,
                                                                       Long eventId,
                                                                       EventRequestStatusRequest eventRequestStatusRequest);

    EventFullDto getEvent(Long id);

    List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId);

    EventFullDto getEventUser(Long userId, Long eventId);

    List<EventShortDto> getEvents(GetEventsRequest request);

    List<EventFullDto> getEventsAdmin(GetEventsForAdminRequest request);

    List<EventShortDto> getEventsUser(Long userId, Integer from, Integer size);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    Boolean existsByCategoryId(Long eventId);

    EventShortDto getEventById(Long id);

    List<EventShortDto> getAllById(List<Long> ids);

    String getStatus(Long eventId, Long userId, String status, Long count);
}
