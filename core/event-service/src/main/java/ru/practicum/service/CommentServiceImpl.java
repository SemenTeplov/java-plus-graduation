package main.java.ru.practicum.service;

import jakarta.validation.ValidationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import main.java.ru.practicum.constant.Exceptions;
import main.java.ru.practicum.constant.Messages;
import main.java.ru.practicum.dto.EventShortDto;
import main.java.ru.practicum.dto.ParticipationRequestDto;
import main.java.ru.practicum.dto.RequestCommentDto;
import main.java.ru.practicum.dto.ResponseCommentDto;
import main.java.ru.practicum.dto.UserShortDto;
import main.java.ru.practicum.exception.NotFoundException;
import main.java.ru.practicum.externel.RequestClient;
import main.java.ru.practicum.externel.UserClient;
import main.java.ru.practicum.mapper.CommentMapper;
import main.java.ru.practicum.mapper.EventMapper;
import main.java.ru.practicum.persistence.entity.Comment;
import main.java.ru.practicum.persistence.entity.Event;
import main.java.ru.practicum.persistence.repository.CommentRepository;
import main.java.ru.practicum.persistence.repository.EventRepository;
import main.java.ru.practicum.persistence.status.State;
import main.java.ru.practicum.persistence.status.StatusRequest;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    private final UserClient userClient;

    private final RequestClient requestClient;

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    @Override
    public ResponseCommentDto addComment(Long userId, Long eventId, RequestCommentDto request) {

        log.info(Messages.MESSAGE_ADD_COMMENT, userId, eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(Exceptions.EXCEPTION_EVENT_NOT_FOUND, eventId)));

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ValidationException(Exceptions.EXCEPTION_COMMENT_NOT_PUBLISHED);
        }

        UserShortDto author = userClient.getUserById(userId).getBody();
        EventShortDto eventDto = eventMapper.eventToEventShortDto(event);

        assert eventDto != null;
        eventDto.setConfirmedRequests(
                requestClient.countByEventIdAndStatus(eventId, StatusRequest.CONFIRMED.toString()).getBody());

        return commentMapper.toCommentDto(commentRepository
                        .save(commentMapper.toComment(request, author, eventDto)), author, eventDto);
    }

    @Override
    public ResponseCommentDto updateComment(Long userId, Long eventId, Long commentId, RequestCommentDto request) {

        log.info(Messages.MESSAGE_UPDATE_COMMENT, userId, eventId, commentId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(Exceptions.EXCEPTION_EVENT_NOT_FOUND, eventId)));

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ValidationException(Exceptions.EXCEPTION_COMMENT_NOT_PUBLISHED);
        }

        UserShortDto author = userClient.getUserById(userId).getBody();
        EventShortDto eventDto = eventMapper.eventToEventShortDto(event);

        assert eventDto != null;
        eventDto.setConfirmedRequests(
                requestClient.countByEventIdAndStatus(eventId, StatusRequest.CONFIRMED.toString()).getBody());

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format(Exceptions.EXCEPTION_COMMENT_NOT_FOUND, commentId)));

        if (!comment.getAuthorId().equals(userId)) {
            throw new ValidationException(Exceptions.EXCEPTION_ONLY_AUTHOR_CAN_EDIT);
        }

        if (!comment.getEventId().equals(eventId)) {
            throw new ValidationException(Exceptions.EXCEPTION_COMMENT_FOR_OTHER_EVENT);
        }

        comment.setText(request.text());
        comment.setEdited(LocalDateTime.now());
        comment = commentRepository.save(comment);

        return commentMapper.toCommentDto(comment, author, eventDto);
    }

    @Override
    public List<ResponseCommentDto> getCommentsByAuthor(Long userId, Integer from, Integer size) {

        log.info(Messages.MESSAGE_GET_COMMENTS_BY_AUTHOR, userId);

        userClient.getUserById(userId);

        List<ParticipationRequestDto> requests = requestClient.getAll().getBody();

        return commentRepository.findAllByAuthorId(userId, PageRequest.of(from / size, size))
                .stream()
                .map(comment -> {
                    UserShortDto userShort = userClient.getUserById(comment.getAuthorId()).getBody();

                    assert requests != null;
                    Long confirmedRequests = requests.stream()
                            .filter(r -> r.getEvent().equals(comment.getEventId())
                                    && r.getStatus().equals(StatusRequest.CONFIRMED.toString())).count();

                    EventShortDto eventShort = createEventShortDtoWithConfirmedRequests(
                            comment.getEventId(), confirmedRequests);

                    return commentMapper.toCommentDto(comment, userShort, eventShort);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseCommentDto> getComments(Long eventId, Integer from, Integer size) {

        log.info(Messages.MESSAGE_GET_COMMENTS_BY_EVENT, eventId);

        EventShortDto eventShort = createEventShortDtoWithConfirmedRequests(eventId, requestClient
                .countByEventIdAndStatus(eventId, StatusRequest.CONFIRMED.toString()).getBody());

        return commentRepository.findAllByEventId(eventId, PageRequest.of(from / size, size))
                .stream()
                .map(comment -> commentMapper
                        .toCommentDto(comment, userClient.getUserById(comment.getAuthorId()).getBody(), eventShort))
                .collect(Collectors.toList());
    }

    @Override
    public ResponseCommentDto getCommentById(Long commentId) {

        log.info(Messages.MESSAGE_GET_COMMENT_BY_ID, commentId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND));
        Long confirmedRequests = requestClient.countByEventIdAndStatus(
                comment.getEventId(), StatusRequest.CONFIRMED.toString()).getBody();
        EventShortDto eventShort = createEventShortDtoWithConfirmedRequests(
                comment.getEventId(), confirmedRequests);

        return commentMapper
                .toCommentDto(comment, userClient.getUserById(comment.getAuthorId()).getBody(), eventShort);
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {

        log.info(Messages.MESSAGE_DELETE_COMMENT, userId, commentId);

        userClient.getUserById(userId);

        if (!commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND))
                .getAuthorId().equals(userId)) {
            throw new ValidationException(Exceptions.EXCEPTION_ONLY_AUTHOR_CAN_DELETE);
        }

        commentRepository.deleteById(commentId);
    }

    @Override
    public void deleteComment(Long commentId) {

        log.info(Messages.MESSAGE_DELETE_COMMENT_BY_ADMIN, commentId);

        commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND));
        commentRepository.deleteById(commentId);
    }

    private EventShortDto createEventShortDtoWithConfirmedRequests(Long id, Long confirmedRequests) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(Exceptions.EXCEPTION_EVENT_NOT_FOUND, id)));

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ValidationException(Exceptions.EXCEPTION_COMMENT_NOT_PUBLISHED);
        }

        EventShortDto eventDto = eventMapper.eventToEventShortDto(event);

        assert eventDto != null;
        eventDto.setConfirmedRequests(confirmedRequests);
        eventDto.setRating(event.getRating() != 0.0 ? event.getRating() : 0.0);

        return eventDto;
    }
}
