package main.java.ru.practicum.service;

import jakarta.ws.rs.ForbiddenException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import main.dto.ParticipationRequestDto;
import main.java.ru.practicum.constant.Exceptions;
import main.java.ru.practicum.constant.Messages;
import main.java.ru.practicum.exception.NotFoundException;
import main.java.ru.practicum.external.EventClient;
import main.java.ru.practicum.external.UserClient;
import main.java.ru.practicum.mapper.RequestMapper;
import main.java.ru.practicum.persistence.entity.Request;
import main.java.ru.practicum.persistence.repository.RequestRepository;
import main.java.ru.practicum.persistence.status.State;
import main.status.Status;

import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final RequestMapper requestMapper;

    private final UserClient userClient;

    private final EventClient eventClient;

    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {

        String status = eventClient
                .getStatus(eventId, userId, State.PUBLISHED.toString(),
                        requestRepository.countByEventIdAndStatus(eventId, Status.CONFIRMED.toString()))
                .getBody();

        if (!requestRepository.getRequestsByUserIdAndEventId(userId, eventId).isEmpty()) {
            throw new ForbiddenException(Exceptions.EXCEPTION_REQUEST_EXIST);
        }

        Request request = requestMapper.toNewRequest(userId, eventId, status);

        log.info(Messages.MESSAGE_ADDED_REQUEST, request.getId(), request.getStatus());

        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {

        log.info(Messages.MESSAGE_CANCEL_REQUEST, requestId, userId);

        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND));
        request.setStatus(String.valueOf(Status.CANCELED));

        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByUser(Long userId) {

        log.info(Messages.MESSAGE_GET_REQUESTS, userId);

        userClient.checkUser(userId);

        return requestRepository.findAllByRequesterId(userId).stream()
                .map(requestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    public Long countByEventIdAndStatus(Long eventId, String status) {
        return requestRepository.countByEventIdAndStatus(eventId, status);
    }

    @Override
    public List<ParticipationRequestDto> getAll() {
        return requestRepository.findAll().stream()
                .map(requestMapper::toParticipationRequestDto).toList();
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByIds(List<Long> ids) {
        return requestRepository.getRequestsByIds(ids.toArray(Long[]::new)).stream()
                .map(requestMapper::toParticipationRequestDto).toList();
    }

    @Override
    public List<ParticipationRequestDto> addParticipationRequests(List<ParticipationRequestDto> list) {

        List<Request> requests = requestRepository.saveAll(list.stream().map(requestMapper::toRequest).toList());

        return requests.stream().map(requestMapper::toParticipationRequestDto).toList();
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByEvent(Long eventId) {
        return requestRepository.getRequestsByEventId(eventId).stream()
                .map(requestMapper::toParticipationRequestDto).toList();
    }
}
