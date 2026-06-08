package com.devconnect.eventservice.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EventCreatedEvent {
    private UUID id;
    private String title;
    private UUID organizerId;
    private String type;
    private LocalDateTime startDate;
}
