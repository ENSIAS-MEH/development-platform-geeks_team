package com.techhub.notification_service.dto;

import com.techhub.notification_service.entity.NotificationStatus;
import com.techhub.notification_service.entity.NotificationType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {

    private UUID id;
    private UUID userId;
    private String recipientEmail;
    private String displayName;
    private NotificationType type;
    private NotificationStatus status;
    private String failureReason;
    private String title;
    private String message;
    private boolean read;
    private String eventId;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
}