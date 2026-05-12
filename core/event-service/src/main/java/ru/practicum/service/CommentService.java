package main.java.ru.practicum.service;

import main.java.ru.practicum.dto.RequestCommentDto;
import main.java.ru.practicum.dto.ResponseCommentDto;

import java.util.List;

public interface CommentService {

    ResponseCommentDto addComment(Long userId, Long eventId, RequestCommentDto newCommentDto);

    ResponseCommentDto updateComment(Long userId, Long eventId, Long commentId, RequestCommentDto newCommentDto);

    List<ResponseCommentDto> getCommentsByAuthor(Long userId, Integer from, Integer size);

    List<ResponseCommentDto> getComments(Long eventId, Integer from, Integer size);

    ResponseCommentDto getCommentById(Long commentId);

    void deleteComment(Long userId, Long commentId);

    void deleteComment(Long commentId);
}
