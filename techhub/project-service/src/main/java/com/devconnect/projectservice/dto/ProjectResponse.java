package com.devconnect.projectservice.dto;

import com.devconnect.projectservice.enums.ProjectStatus;
import com.devconnect.projectservice.enums.ProjectType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import java.io.Serializable;

@Data @Builder
public class ProjectResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private String title;
    private String description;
    private ProjectType type;
    private Set<String> technologies;
    private Set<String> skillsNeeded;
    private ProjectStatus status;
    private String githubUrl;
    private UUID ownerId;
    private long memberCount;
    private boolean userIsMember;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
