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
public class CommentAddedEvent {
    private UUID commentId;
    private UUID postId;
    private UUID authorId;
    private String content;
    private UUID parentCommentId;
    private Instant createdAt;
}
