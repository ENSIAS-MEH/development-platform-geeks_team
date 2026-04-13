package com.techhub.community.dto;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Response body for a comment.
 * Includes nested replies (max depth 2).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse implements Serializable {

    private UUID id;
    private UUID postId;
    private UUID authorId;
    private String content;
    private UUID parentCommentId;
    private Integer upvotes;
    private Instant createdAt;

    /** Nested replies – only populated for top-level comments */
    private List<CommentResponse> replies;
}
