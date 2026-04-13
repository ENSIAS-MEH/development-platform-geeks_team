package com.techhub.community.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

/**
 * Request body for creating a comment or a reply.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequest {

    @NotBlank(message = "Comment content is required")
    private String content;

    /**
     * If set, this comment is a reply to another comment.
     * Max nesting depth is 2 (enforced at the service layer).
     */
    private UUID parentCommentId;
}
