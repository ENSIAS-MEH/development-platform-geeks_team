package com.techhub.teamservice.repository;

import com.techhub.teamservice.entity.Team;
import com.techhub.teamservice.entity.TeamMember;
import com.techhub.teamservice.entity.enums.MemberRole;
import com.techhub.teamservice.entity.enums.TeamStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository slice test for {@link TeamMemberRepository}.
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("TeamMemberRepository — custom queries")
class TeamMemberRepositoryTest {

    @Autowired
    private TeamMemberRepository memberRepository;

    @Autowired
    private TestEntityManager em;

    private static final UUID TEAM_ID  = UUID.randomUUID();
    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID USER_1   = UUID.randomUUID();
    private static final UUID USER_2   = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        em.persistAndFlush(Team.builder()
                .id(TEAM_ID).name("Test Team")
                .maxMembers(5).currentMembers(3)
                .status(TeamStatus.OPEN).ownerId(OWNER_ID)
                .createdAt(Instant.now()).updatedAt(Instant.now())
                .build());

        em.persistAndFlush(TeamMember.builder()
                .id(UUID.randomUUID()).teamId(TEAM_ID)
                .userId(OWNER_ID).role(MemberRole.OWNER)
                .joinedAt(Instant.now().minusSeconds(100))
                .build());

        em.persistAndFlush(TeamMember.builder()
                .id(UUID.randomUUID()).teamId(TEAM_ID)
                .userId(USER_1).role(MemberRole.MEMBER)
                .joinedAt(Instant.now().minusSeconds(50))
                .build());

        em.persistAndFlush(TeamMember.builder()
                .id(UUID.randomUUID()).teamId(TEAM_ID)
                .userId(USER_2).role(MemberRole.MEMBER)
                .joinedAt(Instant.now())
                .build());

        em.clear();
    }

    @Test
    @DisplayName("existsByTeamIdAndUserId() returns true for existing member")
    void existsByTeamAndUser_true() {
        assertThat(memberRepository.existsByTeamIdAndUserId(TEAM_ID, USER_1)).isTrue();
    }

    @Test
    @DisplayName("existsByTeamIdAndUserId() returns false for non-member")
    void existsByTeamAndUser_false() {
        assertThat(memberRepository.existsByTeamIdAndUserId(TEAM_ID, UUID.randomUUID()))
                .isFalse();
    }

    @Test
    @DisplayName("existsByTeamIdAndUserIdAndRole() returns true for owner")
    void existsByTeamAndUserAndRole_owner() {
        assertThat(memberRepository.existsByTeamIdAndUserIdAndRole(
                TEAM_ID, OWNER_ID, MemberRole.OWNER)).isTrue();
    }

    @Test
    @DisplayName("existsByTeamIdAndUserIdAndRole() returns false for wrong role")
    void existsByTeamAndUserAndRole_wrongRole() {
        assertThat(memberRepository.existsByTeamIdAndUserIdAndRole(
                TEAM_ID, USER_1, MemberRole.OWNER)).isFalse();
    }

    @Test
    @DisplayName("findByTeamIdOrderByJoinedAtAsc() returns members ordered by joinedAt")
    void findByTeamId_orderedByJoinedAt() {
        List<TeamMember> members = memberRepository
                .findByTeamIdOrderByJoinedAtAsc(TEAM_ID);

        assertThat(members).hasSize(3);
        // First joined should be the owner (joinedAt most in the past)
        assertThat(members.get(0).getUserId()).isEqualTo(OWNER_ID);
        assertThat(members.get(2).getUserId()).isEqualTo(USER_2);
    }

    @Test
    @DisplayName("findByTeamIdAndUserId() returns the specific membership")
    void findByTeamAndUser_returnsRecord() {
        Optional<TeamMember> result = memberRepository
                .findByTeamIdAndUserId(TEAM_ID, USER_1);

        assertThat(result).isPresent();
        assertThat(result.get().getRole()).isEqualTo(MemberRole.MEMBER);
    }

    @Test
    @DisplayName("findByTeamIdAndUserId() returns empty for non-member")
    void findByTeamAndUser_emptyForNonMember() {
        Optional<TeamMember> result = memberRepository
                .findByTeamIdAndUserId(TEAM_ID, UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("countByTeamId() returns correct member count")
    void countByTeamId_correct() {
        assertThat(memberRepository.countByTeamId(TEAM_ID)).isEqualTo(3L);
    }

    @Test
    @DisplayName("deleteAllByTeamId() removes all members of a team")
    void deleteAllByTeamId_removesAll() {
        memberRepository.deleteAllByTeamId(TEAM_ID);
        em.flush();
        assertThat(memberRepository.countByTeamId(TEAM_ID)).isZero();
    }

    @Test
    @DisplayName("findByUserId() returns all teams a user belongs to")
    void findByUserId_returnsMemberships() {
        List<TeamMember> memberships = memberRepository.findByUserId(USER_1);
        assertThat(memberships).hasSize(1);
        assertThat(memberships.get(0).getTeamId()).isEqualTo(TEAM_ID);
    }
}
