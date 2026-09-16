package com.techhub.notification_service.dto;

import com.techhub.notification_service.entity.NotificationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {

    @NotNull
    private UUID userId;

    @NotBlank
    @Email
    private String recipientEmail;

    @NotBlank
    private String displayName;

    @NotNull
    private NotificationType type;
}