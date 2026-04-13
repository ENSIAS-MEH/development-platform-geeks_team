package com.techhub.community.dto;

import com.techhub.community.enums.Topic;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Response body returned when reading a community group.
 * Implements Serializable for Redis cache storage.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupResponse implements Serializable {

    private UUID id;
    private String name;
    private String description;
    private Topic topic;
    private Boolean isPublic;
    private UUID ownerId;
    private Integer memberCount;
    private Instant createdAt;
}
