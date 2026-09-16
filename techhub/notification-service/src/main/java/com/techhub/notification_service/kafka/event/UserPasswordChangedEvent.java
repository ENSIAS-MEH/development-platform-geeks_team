package com.techhub.notification_service.kafka.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserPasswordChangedEvent {

    private String eventId;
    private String eventType;
    private String timestamp;
    private UUID userId;
    private String displayName;
    private String email;
}