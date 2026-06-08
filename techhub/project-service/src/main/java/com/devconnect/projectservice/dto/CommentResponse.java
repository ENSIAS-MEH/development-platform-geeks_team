package com.devconnect.projectservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder
public class CommentResponse {
    private UUID id;
    private UUID projectId;
    private UUID userId;
    private String content;
    private LocalDateTime createdAt;
}
