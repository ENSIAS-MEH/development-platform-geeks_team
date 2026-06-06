package com.techhub.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreatedEvent {
    private UUID postId;
    private UUID groupId;
    private UUID authorId;
    private String title;
    private String type;
    private Instant createdAt;
}
