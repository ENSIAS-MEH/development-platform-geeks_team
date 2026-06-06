package com.techhub.teamservice.repository;

import com.techhub.teamservice.entity.Team;
import com.techhub.teamservice.entity.TeamMember;
import com.techhub.teamservice.entity.enums.MemberRole;
import com.techhub.teamservice.entity.enums.TeamStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
 * Repository slice test for {@link TeamRepository}.
 *
 * <p>{@code @DataJpaTest} starts only the JPA slice (no web, no Kafka, no Redis).
 * H2 in PostgreSQL compatibility mode runs the actual queries.
 * {@code TestEntityManager} persists fixtures directly — bypasses service logic.
 *
 * <p>Note: Native SQL queries (incrementMemberCount, decrementMemberCount) use
 * PostgreSQL-specific syntax and are skipped here — they are tested in the
 * integration test (Step 5, Testcontainers with real PostgreSQL).
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("TeamRepository — custom queries")
class TeamRepositoryTest {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private TestEntityManager em;

    // ── Fixed UUIDs matching data.sql seed ────────────────────────
    private static final UUID OWNER_1 = UUID.fromString("u1000000-0000-0000-0000-000000000001"
            .replace("u", "a"));
    private static final UUID OWNER_2 = UUID.fromString("a1000000-0000-0000-0000-000000000002");
    private static final UUID MEMBER_USER = UUID.randomUUID();

    private Team teamOpen;
    private Team teamFull;

    @BeforeEach
    void setUp() {
        teamOpen = em.persistAndFlush(Team.builder()
                .id(UUID.randomUUID())
                .name("Open Team")
                .description("Test open team")
                .maxMembers(5)
                .currentMembers(2)
                .status(TeamStatus.OPEN)
                .ownerId(OWNER_1)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        teamFull = em.persistAndFlush(Team.builder()
                .id(UUID.randomUUID())
                .name("Full Team")
                .description("Test full team")
                .maxMembers(3)
                .currentMembers(3)
                .status(TeamStatus.FULL)
                .ownerId(OWNER_2)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        // Add MEMBER_USER as a member of teamOpen (not owner)
        em.persistAndFlush(TeamMember.builder()
                .id(UUID.randomUUID())
                .teamId(teamOpen.getId())
                .userId(MEMBER_USER)
                .role(MemberRole.MEMBER)
                .joinedAt(Instant.now())
                .build());

        em.clear();
    }

    // ══ findByOwnerIdOrderByCreatedAtDesc ═════════════════════════

    @Nested
    @DisplayName("findByOwnerIdOrderByCreatedAtDesc()")
    class FindByOwnerIdTests {

        @Test
        @DisplayName("returns teams owned by the user")
        void returnsOwnedTeams() {
            List<Team> result = teamRepository.findByOwnerIdOrderByCreatedAtDesc(OWNER_1);
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("Open Team");
        }

        @Test
        @DisplayName("returns empty list for unknown owner")
        void returnsEmptyForUnknownOwner() {
            List<Team> result = teamRepository.findByOwnerIdOrderByCreatedAtDesc(UUID.randomUUID());
            assertThat(result).isEmpty();
        }
    }

    // ══ findAllTeamsForUser ═══════════════════════════════════════

    @Nested
    @DisplayName("findAllTeamsForUser()")
    class FindAllTeamsForUserTests {

        @Test
        @DisplayName("returns owned AND member teams without duplicates")
        void returnsOwnedAndMemberTeams() {
            // OWNER_1 owns teamOpen; MEMBER_USER is a member of teamOpen
            List<Team> ownerResult = teamRepository.findAllTeamsForUser(OWNER_1);
            assertThat(ownerResult).hasSize(1)
                    .extracting(Team::getName).contains("Open Team");

            List<Team> memberResult = teamRepository.findAllTeamsForUser(MEMBER_USER);
            assertThat(memberResult).hasSize(1)
                    .extracting(Team::getName).contains("Open Team");
        }

        @Test
        @DisplayName("returns empty list for user with no teams")
        void returnsEmptyForUserWithNoTeams() {
            List<Team> result = teamRepository.findAllTeamsForUser(UUID.randomUUID());
            assertThat(result).isEmpty();
        }
    }

    // ══ findByStatusOrderByCreatedAtDesc ══════════════════════════

    @Nested
    @DisplayName("findByStatusOrderByCreatedAtDesc()")
    class FindByStatusTests {

        @Test
        @DisplayName("returns only OPEN teams")
        void returnsOpenTeams() {
            List<Team> result = teamRepository.findByStatusOrderByCreatedAtDesc(TeamStatus.OPEN);
            assertThat(result).allMatch(t -> t.getStatus() == TeamStatus.OPEN);
            assertThat(result).extracting(Team::getName).contains("Open Team");
        }

        @Test
        @DisplayName("returns only FULL teams")
        void returnsFullTeams() {
            List<Team> result = teamRepository.findByStatusOrderByCreatedAtDesc(TeamStatus.FULL);
            assertThat(result).allMatch(t -> t.getStatus() == TeamStatus.FULL);
            assertThat(result).extracting(Team::getName).contains("Full Team");
        }
    }

    // ══ existsByOwnerIdAndNameIgnoreCase ══════════════════════════

    @Nested
    @DisplayName("existsByOwnerIdAndNameIgnoreCase()")
    class ExistsByOwnerAndNameTests {

        @Test
        @DisplayName("returns true for existing team name (case insensitive)")
        void trueForExistingNameCaseInsensitive() {
            assertThat(teamRepository
                    .existsByOwnerIdAndNameIgnoreCase(OWNER_1, "open team")).isTrue();
            assertThat(teamRepository
                    .existsByOwnerIdAndNameIgnoreCase(OWNER_1, "OPEN TEAM")).isTrue();
        }

        @Test
        @DisplayName("returns false for different owner same name")
        void falseForDifferentOwner() {
            assertThat(teamRepository
                    .existsByOwnerIdAndNameIgnoreCase(UUID.randomUUID(), "Open Team")).isFalse();
        }

        @Test
        @DisplayName("returns false for different name")
        void falseForDifferentName() {
            assertThat(teamRepository
                    .existsByOwnerIdAndNameIgnoreCase(OWNER_1, "Unknown Team")).isFalse();
        }
    }

    // ══ findByIdAndOwnerId ════════════════════════════════════════

    @Nested
    @DisplayName("findByIdAndOwnerId()")
    class FindByIdAndOwnerIdTests {

        @Test
        @DisplayName("returns team when id and owner match")
        void returnsWhenMatch() {
            Optional<Team> result = teamRepository.findByIdAndOwnerId(
                    teamOpen.getId(), OWNER_1);
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(teamOpen.getId());
        }

        @Test
        @DisplayName("returns empty when owner does not match")
        void emptyWhenOwnerMismatch() {
            Optional<Team> result = teamRepository.findByIdAndOwnerId(
                    teamOpen.getId(), UUID.randomUUID());
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns empty for unknown id")
        void emptyForUnknownId() {
            Optional<Team> result = teamRepository.findByIdAndOwnerId(
                    UUID.randomUUID(), OWNER_1);
            assertThat(result).isEmpty();
        }
    }
}
