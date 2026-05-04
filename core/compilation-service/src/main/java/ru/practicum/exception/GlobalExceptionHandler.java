package main.java.ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;

import main.dto.ApiError;
import main.java.ru.practicum.constant.Exceptions;
import main.java.ru.practicum.constant.Messages;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler({NotFoundCompletion.class})
    public ResponseEntity<ApiError> handleNotFoundCompletion(NotFoundCompletion ex) {
        log.warn(Messages.NOT_FOUND_COMPLETION, ex);
        ApiError body = new ApiError(Arrays.stream(
                ex.getStackTrace()).map(String::valueOf).toList(),
                Exceptions.NOT_FOUND_COMPLETION,
                Messages.NOT_FOUND_COMPLETION,
                HttpStatus.NOT_FOUND.value() + " " + HttpStatus.NOT_FOUND.getReasonPhrase(),
                LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException  e) {
        log.info(Exceptions.EXCEPTION_NOT_ILLEGAL_ARGUMENT, e.getMessage(), e);

        ApiError error = ApiError.builder()
                .errors(Arrays.stream(e.getStackTrace()).map(String::valueOf).toList())
                .reason(Exceptions.EXCEPTION_NOT_ILLEGAL_ARGUMENT)
                .message(Exceptions.EXCEPTION_NOT_ILLEGAL_ARGUMENT)
                .status(HttpStatus.BAD_REQUEST.value() + " " + HttpStatus.BAD_REQUEST.getReasonPhrase())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception e) {
        log.info(Messages.MESSAGE_INTERNAL_SERVER, e.getMessage(), e);

        ApiError error = ApiError.builder()
                .errors(Arrays.stream(e.getStackTrace()).map(String::valueOf).toList())
                .reason(Messages.MESSAGE_INTERNAL_SERVER)
                .message(Exceptions.EXCEPTION_INTERNAL_SERVER)
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value() + " " + HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
