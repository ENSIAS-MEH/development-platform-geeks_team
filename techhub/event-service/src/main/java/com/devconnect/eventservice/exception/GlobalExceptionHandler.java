package com.devconnect.eventservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Centralized exception handler for the Event Service.
 * Never exposes internal stack traces.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EventNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(build(404, ex.getMessage(), null));
    }

    @ExceptionHandler(AlreadyRegisteredException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyRegistered(AlreadyRegisteredException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(build(409, ex.getMessage(), null));
    }

    @ExceptionHandler(EventFullException.class)
    public ResponseEntity<ErrorResponse> handleFull(EventFullException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(build(409, ex.getMessage(), null));
    }

    @ExceptionHandler(EventNotPublishedException.class)
    public ResponseEntity<ErrorResponse> handleNotPublished(EventNotPublishedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(build(400, ex.getMessage(), null));
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedActionException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(build(403, ex.getMessage(), null));
    }

    @ExceptionHandler(InvalidEventDatesException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDates(InvalidEventDatesException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(build(400, ex.getMessage(), null));
    }

    @ExceptionHandler(RegistrationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRegNotFound(RegistrationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(build(404, ex.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> details = new LinkedHashMap<>();
        for (FieldError err : ex.getBindingResult().getFieldErrors()) {
            details.put(err.getField(), err.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(build(400, "Validation failed", details));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(build(500, "An internal error occurred", null));
    }

    private ErrorResponse build(int status, String message, Map<String, String> details) {
        return ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(status)
            .message(message)
            .details(details)
            .build();
    }
}
