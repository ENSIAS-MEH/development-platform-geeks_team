package com.techhub.community.dto;

import com.techhub.community.enums.PostType;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Response body returned when reading a post.
 * Implements Serializable for Redis cache storage (popular posts cache).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse implements Serializable {

    private UUID id;
    private UUID groupId;
    private UUID authorId;
    private String title;
    private String content;
    private PostType type;
    private Integer upvotes;
    private Integer commentCount;
    private Boolean isPinned;
    private Instant createdAt;
    private Instant updatedAt;
}
