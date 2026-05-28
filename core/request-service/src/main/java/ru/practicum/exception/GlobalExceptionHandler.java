package main.java.ru.practicum.exception;

import jakarta.validation.ValidationException;

import lombok.extern.slf4j.Slf4j;

import main.java.ru.practicum.constant.Exceptions;
import main.java.ru.practicum.constant.Messages;
import main.java.ru.practicum.dto.ApiError;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException e) {
        log.info(Messages.MESSAGE_NOT_FOUND, e.getMessage());

        ApiError error = ApiError.builder()
                .errors(Arrays.stream(e.getStackTrace()).map(String::valueOf).toList())
                .reason(Messages.MESSAGE_NOT_FOUND)
                .message(Exceptions.EXCEPTION_NOT_FOUND)
                .status(HttpStatus.NOT_FOUND.value() + " " + HttpStatus.NOT_FOUND.getReasonPhrase())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler({ForbiddenException.class})
    public ResponseEntity<ApiError> handleForbiddenException(ForbiddenException ex) {
        log.warn(Exceptions.EXCEPTION_REQUEST_EXIST, ex);
        ApiError body = new ApiError(Arrays.stream(
                ex.getStackTrace()).map(String::valueOf).toList(),
                Exceptions.EXCEPTION_REQUEST_EXIST,
                Exceptions.EXCEPTION_REQUEST_EXIST,
                HttpStatus.CONFLICT.value() + " " + HttpStatus.CONFLICT.getReasonPhrase(),
                LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
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

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidationException(ValidationException e) {
        log.info(Messages.MESSAGE_ERROR_VALIDATION, e.getMessage());

        ApiError error = ApiError.builder()
                .errors(Arrays.stream(e.getStackTrace()).map(String::valueOf).toList())
                .reason(Exceptions.EXCEPTION_WRONG_REQUEST)
                .message(e.getMessage())
                .status(HttpStatus.BAD_REQUEST.value() + " " + HttpStatus.BAD_REQUEST.getReasonPhrase())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn(Messages.METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION, ex);
        ApiError body = new ApiError(Arrays.stream(
                ex.getStackTrace()).map(String::valueOf).toList(),
                Exceptions.METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION,
                Messages.METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION,
                HttpStatus.BAD_REQUEST.value() + " " + HttpStatus.BAD_REQUEST.getReasonPhrase(),
                LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException  e) {
        log.info(Messages.MESSAGE_NOT_VALID, e.getMessage(), e);

        ApiError error = ApiError.builder()
                .errors(Arrays.stream(e.getStackTrace()).map(String::valueOf).toList())
                .reason(Messages.MESSAGE_NOT_VALID)
                .message(Exceptions.EXCEPTION_NOT_VALID)
                .status(HttpStatus.BAD_REQUEST.value() + " " + HttpStatus.BAD_REQUEST.getReasonPhrase())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MissingServletRequestParameterException  e) {
        log.info(Messages.MESSAGE_NOT_VALID, e.getMessage(), e);

        ApiError error = ApiError.builder()
                .errors(Arrays.stream(e.getStackTrace()).map(String::valueOf).toList())
                .reason(Messages.MESSAGE_NOT_VALID)
                .message(Exceptions.EXCEPTION_NOT_VALID)
                .status(HttpStatus.BAD_REQUEST.value() + " " + HttpStatus.BAD_REQUEST.getReasonPhrase())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(NoResourceFoundException  e) {
        log.info(Messages.MESSAGE_NOT_VALID, e.getMessage(), e);

        ApiError error = ApiError.builder()
                .errors(Arrays.stream(e.getStackTrace()).map(String::valueOf).toList())
                .reason(Messages.MESSAGE_NOT_VALID)
                .message(Exceptions.EXCEPTION_NOT_VALID)
                .status(HttpStatus.BAD_REQUEST.value() + " " + HttpStatus.BAD_REQUEST.getReasonPhrase())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler({NotMeetRulesEditionException.class})
    public ResponseEntity<ApiError> handleNotMeetRulesEditionException(NotMeetRulesEditionException ex) {
        log.warn(Exceptions.EXCEPTION_NOT_MEET_RULES, ex);
        ApiError body = new ApiError(Arrays.stream(
                ex.getStackTrace()).map(String::valueOf).toList(),
                Exceptions.EXCEPTION_NOT_MEET_RULES,
                Exceptions.EXCEPTION_NOT_MEET_RULES,
                HttpStatus.CONFLICT.value() + " " + HttpStatus.CONFLICT.getReasonPhrase(),
                LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
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
