package main.java.ru.practicum.controller;

import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import main.java.ru.practicum.dto.CommentDto;
import main.java.ru.practicum.dto.NewCommentDto;
import main.java.ru.practicum.service.CommentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/user/{userId}/comments/{eventId}")
    ResponseEntity<CommentDto> addComment(@PathVariable("userId") Long userId, @PathVariable("eventId") Long eventId,
                                          @Valid @RequestBody NewCommentDto newCommentDto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(userId, eventId, newCommentDto));
    }

    @DeleteMapping("/user/{userId}/comments/{commentId}")
    ResponseEntity<Void> deleteComment(@PathVariable("userId") Long userId, @PathVariable("commentId") Long commentId) {

        commentService.deleteComment(userId, commentId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/admin/comments/{commentId}")
    ResponseEntity<Void> deleteCommentByAdmin(@PathVariable("commentId") Long commentId) {

        commentService.deleteComment(commentId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/comments/{commentId}")
    ResponseEntity<CommentDto> getCommentById(@PathVariable("commentId") Long commentId) {

        return ResponseEntity.ok(commentService.getCommentById(commentId));
    }

    @GetMapping("/comments/event/{eventId}")
    ResponseEntity<List<CommentDto>> getComments(@PathVariable("eventId") Long eventId,
                                                 @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                                 @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        return ResponseEntity.ok(commentService.getComments(eventId, from, size));
    }

    @GetMapping("/user/{userId}/comments")
    ResponseEntity<List<CommentDto>> getCommentsByAuthor(@PathVariable("userId") Long userId,
                                                         @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                                         @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        return ResponseEntity.ok(commentService.getCommentsByAuthor(userId, from, size));
    }

    @PatchMapping("/user/{userId}/comments/{eventId}/{commentId}")
    ResponseEntity<CommentDto> updateComment(@PathVariable("userId") Long userId,
                                             @PathVariable("eventId") Long eventId,
                                             @PathVariable("commentId") Long commentId,
                                             @Valid @RequestBody NewCommentDto newCommentDto) {

        return ResponseEntity.ok(commentService.updateComment(userId, eventId, commentId, newCommentDto));
    }
}
