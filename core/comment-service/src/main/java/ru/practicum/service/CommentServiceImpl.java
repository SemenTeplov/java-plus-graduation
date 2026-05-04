package main.java.ru.practicum.service;

import jakarta.validation.ValidationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import main.dto.EventShortDto;
import main.dto.ParticipationRequestDto;
import main.dto.UserShortDto;
import main.java.ru.practicum.constant.Exceptions;
import main.java.ru.practicum.constant.Messages;
import main.java.ru.practicum.dto.CommentDto;
import main.java.ru.practicum.dto.NewCommentDto;
import main.java.ru.practicum.exception.NotFoundException;
import main.java.ru.practicum.external.EventClient;
import main.java.ru.practicum.external.RequestClient;
import main.java.ru.practicum.external.UserClient;
import main.java.ru.practicum.mapper.CommentMapper;
import main.java.ru.practicum.persistence.entity.Comment;
import main.java.ru.practicum.persistence.repository.CommentRepository;
import main.status.StatusRequest;

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

    private final EventClient eventClient;

    private final RequestClient requestClient;

    @Override
    public CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto) {

        log.info(Messages.MESSAGE_ADD_COMMENT, userId, eventId);

        if (newCommentDto.text().isBlank()) {
            throw new ValidationException(Exceptions.EXCEPTION_COMMENT_IS_EMPTY);
        }

        UserShortDto author = userClient.getUserById(userId).getBody();
        EventShortDto event = eventClient.getEventById(eventId).getBody();

        assert event != null;
        event.setConfirmedRequests(requestClient.countByEventIdAndStatus(eventId, StatusRequest.CONFIRMED.toString()).getBody());

        return commentMapper.toCommentDto(commentRepository
                        .save(commentMapper.toComment(newCommentDto, author, event)), author, event);
    }

    @Override
    public CommentDto updateComment(Long userId, Long eventId, Long commentId, NewCommentDto newCommentDto) {

        log.info(Messages.MESSAGE_UPDATE_COMMENT, userId, eventId, commentId);

        if (newCommentDto.text().isBlank()) {
            throw new ValidationException(Exceptions.EXCEPTION_COMMENT_IS_EMPTY);
        }

        UserShortDto author = userClient.getUserById(userId).getBody();
        EventShortDto event = eventClient.getEventById(eventId).getBody();

        assert event != null;
        event.setConfirmedRequests(requestClient.countByEventIdAndStatus(eventId, StatusRequest.CONFIRMED.toString()).getBody());

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format(Exceptions.EXCEPTION_COMMENT_NOT_FOUND, commentId)));

        if (!comment.getAuthor().equals(userId)) {
            throw new ValidationException(Exceptions.EXCEPTION_ONLY_AUTHOR_CAN_EDIT);
        }

        if (!comment.getEvent().equals(eventId)) {
            throw new ValidationException(Exceptions.EXCEPTION_COMMENT_FOR_OTHER_EVENT);
        }

        comment.setText(newCommentDto.text());
        comment.setEdited(LocalDateTime.now());
        comment = commentRepository.save(comment);

        return commentMapper.toCommentDto(comment, author, event);
    }

    @Override
    public List<CommentDto> getCommentsByAuthor(Long userId, Integer from, Integer size) {

        log.info(Messages.MESSAGE_GET_COMMENTS_BY_AUTHOR, userId);

        userClient.getUserById(userId);

        List<ParticipationRequestDto> requests = requestClient.getAll().getBody();

        return commentRepository.findAllByAuthorId(userId, PageRequest.of(from / size, size))
                .stream()
                .map(comment -> {
                    UserShortDto userShort = userClient.getUserById(comment.getAuthor()).getBody();

                    assert requests != null;
                    Long confirmedRequests = requests.stream()
                            .filter(r -> r.getEvent().equals(comment.getEvent())
                                    && r.getStatus().equals(StatusRequest.CONFIRMED.toString())).count();

                    EventShortDto eventShort = createEventShortDtoWithConfirmedRequests(
                            comment.getEvent(), confirmedRequests);

                    return commentMapper.toCommentDto(comment, userShort, eventShort);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getComments(Long eventId, Integer from, Integer size) {

        log.info(Messages.MESSAGE_GET_COMMENTS_BY_EVENT, eventId);

        EventShortDto eventShort = createEventShortDtoWithConfirmedRequests(eventId, requestClient
                .countByEventIdAndStatus(eventId, StatusRequest.CONFIRMED.toString()).getBody());

        return commentRepository.findAllByEventId(eventId, PageRequest.of(from / size, size))
                .stream()
                .map(comment -> commentMapper
                        .toCommentDto(comment, userClient.getUserById(comment.getAuthor()).getBody(), eventShort))
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentById(Long commentId) {

        log.info(Messages.MESSAGE_GET_COMMENT_BY_ID, commentId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND));
        Long confirmedRequests = requestClient.countByEventIdAndStatus(
                comment.getEvent(), StatusRequest.CONFIRMED.toString()).getBody();
        EventShortDto eventShort = createEventShortDtoWithConfirmedRequests(
                comment.getEvent(), confirmedRequests);

        return commentMapper.toCommentDto(comment, userClient.getUserById(comment.getAuthor()).getBody(), eventShort);
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {

        log.info(Messages.MESSAGE_DELETE_COMMENT, userId, commentId);

        userClient.getUserById(userId);

        if (!commentRepository.findById(commentId).get().getAuthor().equals(userId)) {
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

        EventShortDto event = eventClient.getEventById(id).getBody();

        assert event != null;
        event.setConfirmedRequests(confirmedRequests);
        event.setViews(event.getViews() != null ? event.getViews() : 0L);

        return event;
    }
}
