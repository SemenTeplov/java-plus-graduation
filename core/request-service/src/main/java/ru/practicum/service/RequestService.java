package main.java.ru.practicum.service;

import main.java.ru.practicum.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    ParticipationRequestDto addRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getRequestsByUser(Long userId);

    Long countByEventIdAndStatus (Long eventId, String status);

    List<ParticipationRequestDto> getAll();

    List<ParticipationRequestDto> getRequestsByIds(List<Long> ids);

    List<ParticipationRequestDto> addParticipationRequests(List<ParticipationRequestDto> list);

    List<ParticipationRequestDto> getRequestsByEvent(Long eventId);
}
