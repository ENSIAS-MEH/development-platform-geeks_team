package com.devconnect.eventservice.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserRegisteredEvent {
    private UUID userId;
    private UUID eventId;
    private String eventTitle;
    private UUID organizerId;
}
