package com.techhub.teamservice.entity;

import com.techhub.teamservice.entity.enums.MemberRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link TeamMember} domain helpers.
 * No Spring context — pure POJO tests.
 */
@DisplayName("TeamMember entity — domain logic")
class TeamMemberTest {

    private TeamMember buildMember(MemberRole role) {
        return TeamMember.builder()
                .id(UUID.randomUUID())
                .teamId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .role(role)
                .joinedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("isOwner() returns true for OWNER role")
    void isOwner_trueForOwner() {
        assertThat(buildMember(MemberRole.OWNER).isOwner()).isTrue();
    }

    @Test
    @DisplayName("isOwner() returns false for MEMBER role")
    void isOwner_falseForMember() {
        assertThat(buildMember(MemberRole.MEMBER).isOwner()).isFalse();
    }

    @Test
    @DisplayName("onPersist() assigns UUID when id is null")
    void onPersist_assignsId() {
        TeamMember m = new TeamMember();
        m.onPersist();
        assertThat(m.getId()).isNotNull();
    }

    @Test
    @DisplayName("onPersist() sets default role to MEMBER")
    void onPersist_defaultRoleMember() {
        TeamMember m = new TeamMember();
        m.onPersist();
        assertThat(m.getRole()).isEqualTo(MemberRole.MEMBER);
    }

    @Test
    @DisplayName("onPersist() sets joinedAt timestamp")
    void onPersist_setsJoinedAt() {
        TeamMember m = new TeamMember();
        Instant before = Instant.now().minusSeconds(1);
        m.onPersist();
        assertThat(m.getJoinedAt()).isAfter(before);
    }

    @Test
    @DisplayName("onPersist() preserves existing id")
    void onPersist_preservesExistingId() {
        UUID existing = UUID.randomUUID();
        TeamMember m = new TeamMember();
        m.setId(existing);
        m.onPersist();
        assertThat(m.getId()).isEqualTo(existing);
    }

    @Test
    @DisplayName("equality is based on id only")
    void equality_basedOnId() {
        UUID id = UUID.randomUUID();
        TeamMember a = buildMember(MemberRole.OWNER);
        TeamMember b = buildMember(MemberRole.MEMBER);
        a.setId(id);
        b.setId(id);
        assertThat(a).isEqualTo(b);
    }
}