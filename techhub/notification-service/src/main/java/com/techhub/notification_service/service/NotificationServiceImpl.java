package com.techhub.notification_service.service;

import com.techhub.notification_service.dto.NotificationRequest;
import com.techhub.notification_service.dto.NotificationResponse;
import com.techhub.notification_service.entity.Notification;
import com.techhub.notification_service.entity.NotificationStatus;
import com.techhub.notification_service.entity.NotificationType;
import com.techhub.notification_service.exception.NotificationNotFoundException;
import com.techhub.notification_service.kafka.event.TeamInvitedEvent;
import com.techhub.notification_service.kafka.event.TeamJoinedEvent;
import com.techhub.notification_service.kafka.event.UserPasswordChangedEvent;
import com.techhub.notification_service.kafka.event.UserRegisteredEvent;
import com.techhub.notification_service.mapper.NotificationMapper;
import com.techhub.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper     notificationMapper;
    private final EmailService           emailService;

    // ── Kafka-triggered: user events ────────────────────────────────────────

    @Override
    @Transactional
    public void handleUserRegistered(UserRegisteredEvent event) {
        if (isDuplicate(event.getEventId())) return;

        Notification notification = notificationMapper.fromUserRegisteredEvent(event);
        notificationRepository.save(notification);

        try {
            emailService.sendWelcomeEmail(event.getEmail(), event.getDisplayName(), event.getRole());
            markSent(notification);
        } catch (Exception e) {
            markFailed(notification, e.getMessage());
        }
    }

    @Override
    @Transactional
    public void handleUserPasswordChanged(UserPasswordChangedEvent event) {
        if (isDuplicate(event.getEventId())) return;

        Notification notification = notificationMapper.fromUserPasswordChangedEvent(event);
        notificationRepository.save(notification);

        try {
            emailService.sendPasswordChangedEmail(event.getEmail(), event.getDisplayName());
            markSent(notification);
        } catch (Exception e) {
            markFailed(notification, e.getMessage());
        }
    }

    // ── Kafka-triggered: team events ─────────────────────────────────────────

    @Override
    @Transactional
    public void handleTeamInvited(TeamInvitedEvent event) {
        if (event.getReceiverId() == null) {
            log.warn("[NotificationService] TeamInvitedEvent missing receiverId — skipping");
            return;
        }

        String eventId = event.getInvitationId() != null
                ? event.getInvitationId().toString()
                : UUID.randomUUID().toString();

        if (isDuplicate(eventId)) return;

        Notification notification = notificationMapper.fromTeamInvitedEvent(event);
        notificationRepository.save(notification);
        log.info("[NotificationService] Team invitation notification stored for userId={}", event.getReceiverId());
    }

    @Override
    @Transactional
    public void handleTeamJoined(TeamJoinedEvent event) {
        if (event.getUserId() == null) {
            log.warn("[NotificationService] TeamJoinedEvent missing userId — skipping");
            return;
        }

        String eventId = event.getTeamId() + "_" + event.getUserId();
        if (isDuplicate(eventId)) return;

        Notification notification = notificationMapper.fromTeamJoinedEvent(event);
        notificationRepository.save(notification);
        log.info("[NotificationService] Team joined notification stored for userId={}", event.getUserId());
    }

    // ── REST-triggered handler ────────────────────────────────────────────────

    @Override
    @Transactional
    public NotificationResponse sendManual(NotificationRequest request) {
        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .recipientEmail(request.getRecipientEmail())
                .displayName(request.getDisplayName())
                .type(request.getType())
                .status(NotificationStatus.PENDING)
                .eventId(UUID.randomUUID().toString())
                .eventType("MANUAL")
                .build();

        notificationRepository.save(notification);

        try {
            dispatchByType(request);
            markSent(notification);
        } catch (Exception e) {
            markFailed(notification, e.getMessage());
        }

        return notificationMapper.toResponse(notification);
    }

    // ── Query methods ─────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getByUserId(UUID userId) {
        return notificationRepository.findByUserId(userId)
                .stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getByStatus(NotificationStatus status) {
        return notificationRepository.findByStatus(status)
                .stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ── Read-state methods ────────────────────────────────────────────────────

    @Override
    @Transactional
    public NotificationResponse markAsRead(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));
        notification.setRead(true);
        return notificationMapper.toResponse(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public void markAllAsReadForUser(UUID userId) {
        List<Notification> unread = notificationRepository.findByUserId(userId)
                .stream()
                .filter(n -> !n.isRead())
                .collect(Collectors.toList());
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
        log.info("[NotificationService] Marked {} notifications as read for userId={}", unread.size(), userId);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private boolean isDuplicate(String eventId) {
        if (notificationRepository.existsByEventId(eventId)) {
            log.warn("[NotificationService] Duplicate eventId={} — skipping", eventId);
            return true;
        }
        return false;
    }

    private void markSent(Notification notification) {
        notification.setStatus(NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());
        notificationRepository.save(notification);
        log.info("[NotificationService] Notification SENT id={}", notification.getId());
    }

    private void markFailed(Notification notification, String reason) {
        notification.setStatus(NotificationStatus.FAILED);
        notification.setFailureReason(reason);
        notificationRepository.save(notification);
        log.error("[NotificationService] Notification FAILED id={} reason={}", notification.getId(), reason);
    }

    private void dispatchByType(NotificationRequest request) {
        switch (request.getType()) {
            case WELCOME_EMAIL ->
                emailService.sendWelcomeEmail(request.getRecipientEmail(), request.getDisplayName(), null);
            case PASSWORD_CHANGED_EMAIL ->
                emailService.sendPasswordChangedEmail(request.getRecipientEmail(), request.getDisplayName());
            default ->
                log.info("[NotificationService] Manual send for type={} — no email template", request.getType());
        }
    }
}
