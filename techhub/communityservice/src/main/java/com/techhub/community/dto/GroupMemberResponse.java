package com.techhub.community.dto;

import com.techhub.community.enums.MemberRole;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Response body for a group membership record.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMemberResponse implements Serializable {

    private UUID id;
    private UUID groupId;
    private UUID userId;
    private MemberRole role;
    private Instant joinedAt;
}
