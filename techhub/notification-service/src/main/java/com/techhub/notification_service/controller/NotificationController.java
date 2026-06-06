package com.techhub.notification_service.controller;

import com.techhub.notification_service.dto.NotificationRequest;
import com.techhub.notification_service.dto.NotificationResponse;
import com.techhub.notification_service.entity.NotificationStatus;
import com.techhub.notification_service.exception.NotificationNotFoundException;
import com.techhub.notification_service.repository.NotificationRepository;
import com.techhub.notification_service.mapper.NotificationMapper;
import com.techhub.notification_service.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Query and manage notifications")
public class NotificationController {

    private final NotificationService    notificationService;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper     notificationMapper;

    // ── GET /api/v1/notifications/{id} ───────────────────────────────────────

    @GetMapping("/{id}")
    @Operation(summary = "Get a notification by ID")
    public ResponseEntity<NotificationResponse> getById(@PathVariable UUID id) {
        return notificationRepository.findById(id)
                .map(notificationMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotificationNotFoundException(id));
    }

    // ── GET /api/v1/notifications/user/{userId} ───────────────────────────────

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all notifications for a user")
    public ResponseEntity<List<NotificationResponse>> getByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(notificationService.getByUserId(userId));
    }

    // ── GET /api/v1/notifications?status=FAILED ───────────────────────────────

    @GetMapping
    @Operation(summary = "Get all notifications, optionally filtered by status")
    public ResponseEntity<List<NotificationResponse>> getAll(
            @RequestParam(required = false) NotificationStatus status) {

        if (status != null) {
            return ResponseEntity.ok(notificationService.getByStatus(status));
        }
        return ResponseEntity.ok(
                notificationRepository.findAll()
                        .stream()
                        .map(notificationMapper::toResponse)
                        .toList()
        );
    }

    // ── POST /api/v1/notifications/send ──────────────────────────────────────

    @PostMapping("/send")
    @Operation(summary = "Manually trigger a notification (for testing or admin use)")
    public ResponseEntity<NotificationResponse> sendManual(
            @Valid @RequestBody NotificationRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(notificationService.sendManual(request));
    }

    // ── PUT /api/v1/notifications/{id}/read ──────────────────────────────────

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark a single notification as read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable UUID id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    // ── PUT /api/v1/notifications/user/{userId}/read-all ─────────────────────

    @PutMapping("/user/{userId}/read-all")
    @Operation(summary = "Mark all notifications for a user as read")
    public ResponseEntity<Void> markAllAsRead(@PathVariable UUID userId) {
        notificationService.markAllAsReadForUser(userId);
        return ResponseEntity.noContent().build();
    }
}
