package com.devconnect.projectservice.dto;

import com.devconnect.projectservice.enums.MemberRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder
public class ProjectMemberResponse {
    private UUID id;
    private UUID projectId;
    private UUID userId;
    private MemberRole role;
    private LocalDateTime joinedAt;
}
