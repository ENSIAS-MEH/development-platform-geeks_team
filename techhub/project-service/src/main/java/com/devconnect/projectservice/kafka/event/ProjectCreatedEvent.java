package com.devconnect.projectservice.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProjectCreatedEvent {
    private UUID id;
    private String title;
    private UUID ownerId;
}
