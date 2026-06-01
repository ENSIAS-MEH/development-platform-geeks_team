package com.techhub.teamservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * GlobalExceptionHandler
 *
 * <p>Centralised handler so no stack trace is ever exposed to the client.
 * Each handler logs the full exception internally while returning a clean,
 * structured JSON body.
 *
 * <p>Shape of all error responses:
 * <pre>
 * {
 *   "status":    404,
 *   "error":     "Not Found",
 *   "message":   "Team not found with id: ...",
 *   "path":      "/api/teams/...",
 *   "timestamp": "2024-..."
 * }
 * </pre>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── Domain exceptions ─────────────────────────────────────────────────

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), path(request));
    }

    @ExceptionHandler(TeamFullException.class)
    public ResponseEntity<ErrorResponse> handleTeamFull(
            TeamFullException ex, WebRequest request) {
        log.info("Team capacity exceeded: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, ex.getMessage(), path(request));
    }

    @ExceptionHandler(InvitationExpiredException.class)
    public ResponseEntity<ErrorResponse> handleInvitationExpired(
            InvitationExpiredException ex, WebRequest request) {
        log.info("Expired invitation accessed: {}", ex.getMessage());
        return build(HttpStatus.GONE, ex.getMessage(), path(request));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedException ex, WebRequest request) {
        log.warn("Unauthorized access attempt: {}", ex.getMessage());
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), path(request));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(
            ForbiddenException ex, WebRequest request) {
        log.warn("Forbidden operation attempted: {}", ex.getMessage());
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), path(request));
    }

    @ExceptionHandler(DuplicateInvitationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateInvitation(
            DuplicateInvitationException ex, WebRequest request) {
        log.info("Duplicate invitation: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, ex.getMessage(), path(request));
    }

    // ── Spring Security ───────────────────────────────────────────────────

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, WebRequest request) {
        log.warn("Access denied: {}", ex.getMessage());
        return build(HttpStatus.FORBIDDEN, "You do not have permission to perform this action", path(request));
    }

    // ── Validation (@Valid) ────────────────────────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, WebRequest request) {

        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value",
                        (existing, replacement) -> existing   // keep first error per field
                ));

        log.debug("Validation failed: {}", fieldErrors);

        ValidationErrorResponse body = ValidationErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("One or more fields failed validation")
                .path(path(request))
                .timestamp(Instant.now())
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.badRequest().body(body);
    }

    // ── Catch-all (never expose internals) ────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex, WebRequest request) {
        // Log full stack trace internally — client sees nothing
        log.error("Unhandled exception on [{}]: ", path(request), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.",
                path(request));
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, String path) {
        ErrorResponse body = ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(status).body(body);
    }

    private String path(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}

