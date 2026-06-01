package com.devconnect.eventservice.dto;

import com.devconnect.eventservice.enums.RegistrationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/** Response for a registration record. */
@Data @Builder
public class RegistrationResponse {
    private UUID id;
    private UUID eventId;
    private UUID userId;
    private LocalDateTime registeredAt;
    private RegistrationStatus status;
}
