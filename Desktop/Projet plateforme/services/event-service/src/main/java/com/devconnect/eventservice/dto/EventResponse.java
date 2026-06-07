package com.devconnect.eventservice.dto;

import com.devconnect.eventservice.enums.EventStatus;
import com.devconnect.eventservice.enums.EventType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/** Full event response including participant count and user registration status. */
@Data @Builder
public class EventResponse {
    private UUID id;
    private String title;
    private String description;
    private EventType type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private Integer maxParticipants;
    private UUID organizerId;
    private EventStatus status;
    private Set<String> tags;
    private long participantCount;
    private boolean userRegistered;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
