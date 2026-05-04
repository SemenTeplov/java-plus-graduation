package main.java.ru.practicum.service;

import main.java.ru.practicum.dto.CommentDto;
import main.java.ru.practicum.dto.NewCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateComment(Long userId, Long eventId, Long commentId, NewCommentDto newCommentDto);

    List<CommentDto> getCommentsByAuthor(Long userId, Integer from, Integer size);

    List<CommentDto> getComments(Long eventId, Integer from, Integer size);

    CommentDto getCommentById(Long commentId);

    void deleteComment(Long userId, Long commentId);

    void deleteComment(Long commentId);
}
