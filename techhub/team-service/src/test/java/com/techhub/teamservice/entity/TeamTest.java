package com.techhub.teamservice.entity;

import com.techhub.teamservice.entity.enums.TeamStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link Team} domain helpers.
 *
 * <p>No Spring context loaded — pure POJO tests.
 * These run in milliseconds and give immediate feedback on business logic.
 *
 * <p>Coverage target: 100% of Team domain methods.
 */
@DisplayName("Team entity — domain logic")
class TeamTest {

    // ── Test fixture factory ──────────────────────────────────────

    private Team buildTeam(int current, int max, TeamStatus status) {
        return Team.builder()
                .id(UUID.randomUUID())
                .name("Test Team")
                .maxMembers(max)
                .currentMembers(current)
                .status(status)
                .ownerId(UUID.randomUUID())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    // ══ isFull() ══════════════════════════════════════════════════

    @Nested
    @DisplayName("isFull()")
    class IsFullTests {

        @Test
        @DisplayName("returns true when currentMembers equals maxMembers")
        void isFull_whenCurrentEqualsMax() {
            Team team = buildTeam(5, 5, TeamStatus.FULL);
            assertThat(team.isFull()).isTrue();
        }

        @Test
        @DisplayName("returns true when currentMembers exceeds maxMembers (guard)")
        void isFull_whenCurrentExceedsMax() {
            Team team = buildTeam(6, 5, TeamStatus.FULL);
            assertThat(team.isFull()).isTrue();
        }

        @Test
        @DisplayName("returns false when there is still capacity")
        void isFull_whenBelowMax() {
            Team team = buildTeam(3, 5, TeamStatus.OPEN);
            assertThat(team.isFull()).isFalse();
        }

        @Test
        @DisplayName("returns false when currentMembers is null (defensive)")
        void isFull_whenCurrentMembersNull() {
            Team team = buildTeam(0, 5, TeamStatus.OPEN);
            team.setCurrentMembers(null);
            assertThat(team.isFull()).isFalse();
        }
    }

    // ══ hasCapacity() ═════════════════════════════════════════════

    @Nested
    @DisplayName("hasCapacity()")
    class HasCapacityTests {

        @Test
        @DisplayName("returns true when OPEN and below max")
        void hasCapacity_openAndBelowMax() {
            Team team = buildTeam(3, 5, TeamStatus.OPEN);
            assertThat(team.hasCapacity()).isTrue();
        }

        @Test
        @DisplayName("returns false when FULL")
        void hasCapacity_whenFull() {
            Team team = buildTeam(5, 5, TeamStatus.FULL);
            assertThat(team.hasCapacity()).isFalse();
        }

        @Test
        @DisplayName("returns false when CLOSED even if below max")
        void hasCapacity_whenClosed() {
            Team team = buildTeam(2, 5, TeamStatus.CLOSED);
            assertThat(team.hasCapacity()).isFalse();
        }
    }

    // ══ incrementMembers() ════════════════════════════════════════

    @Nested
    @DisplayName("incrementMembers()")
    class IncrementMembersTests {

        @Test
        @DisplayName("increments count by 1")
        void increment_increasesCount() {
            Team team = buildTeam(3, 5, TeamStatus.OPEN);
            team.incrementMembers();
            assertThat(team.getCurrentMembers()).isEqualTo(4);
        }

        @Test
        @DisplayName("sets status to FULL when max is reached")
        void increment_setsFullWhenMaxReached() {
            Team team = buildTeam(4, 5, TeamStatus.OPEN);
            team.incrementMembers();
            assertThat(team.getCurrentMembers()).isEqualTo(5);
            assertThat(team.getStatus()).isEqualTo(TeamStatus.FULL);
        }

        @Test
        @DisplayName("status stays OPEN when still below max after increment")
        void increment_staysOpenBelowMax() {
            Team team = buildTeam(2, 5, TeamStatus.OPEN);
            team.incrementMembers();
            assertThat(team.getStatus()).isEqualTo(TeamStatus.OPEN);
        }

        @Test
        @DisplayName("handles null currentMembers gracefully (treats as 0)")
        void increment_nullCurrentMembers() {
            Team team = buildTeam(0, 5, TeamStatus.OPEN);
            team.setCurrentMembers(null);
            team.incrementMembers();
            assertThat(team.getCurrentMembers()).isEqualTo(1);
        }
    }

    // ══ decrementMembers() ════════════════════════════════════════

    @Nested
    @DisplayName("decrementMembers()")
    class DecrementMembersTests {

        @Test
        @DisplayName("decrements count by 1")
        void decrement_decreasesCount() {
            Team team = buildTeam(4, 5, TeamStatus.OPEN);
            team.decrementMembers();
            assertThat(team.getCurrentMembers()).isEqualTo(3);
        }

        @Test
        @DisplayName("reopens a FULL team when a member leaves")
        void decrement_reopensFullTeam() {
            Team team = buildTeam(5, 5, TeamStatus.FULL);
            team.decrementMembers();
            assertThat(team.getCurrentMembers()).isEqualTo(4);
            assertThat(team.getStatus()).isEqualTo(TeamStatus.OPEN);
        }

        @Test
        @DisplayName("does not go below 0")
        void decrement_doesNotGoBelowZero() {
            Team team = buildTeam(0, 5, TeamStatus.OPEN);
            team.decrementMembers();
            assertThat(team.getCurrentMembers()).isEqualTo(0);
        }

        @Test
        @DisplayName("CLOSED status is not changed on decrement")
        void decrement_doesNotReopenClosedTeam() {
            Team team = buildTeam(3, 5, TeamStatus.CLOSED);
            team.decrementMembers();
            assertThat(team.getStatus()).isEqualTo(TeamStatus.CLOSED);
        }
    }

    // ══ @PrePersist defaults ══════════════════════════════════════

    @Nested
    @DisplayName("@PrePersist defaults via onPersist()")
    class OnPersistTests {

        @Test
        @DisplayName("assigns a UUID when id is null")
        void onPersist_assignsId() {
            Team team = new Team();
            team.setName("New Team");
            team.setMaxMembers(5);
            team.setOwnerId(UUID.randomUUID());
            team.onPersist();

            assertThat(team.getId()).isNotNull();
        }

        @Test
        @DisplayName("sets status to OPEN by default")
        void onPersist_defaultStatusIsOpen() {
            Team team = new Team();
            team.onPersist();
            assertThat(team.getStatus()).isEqualTo(TeamStatus.OPEN);
        }

        @Test
        @DisplayName("sets currentMembers to 1 by default")
        void onPersist_defaultCurrentMembersIsOne() {
            Team team = new Team();
            team.onPersist();
            assertThat(team.getCurrentMembers()).isEqualTo(1);
        }

        @Test
        @DisplayName("sets createdAt and updatedAt")
        void onPersist_setsTimestamps() {
            Team team = new Team();
            Instant before = Instant.now().minusSeconds(1);
            team.onPersist();
            Instant after = Instant.now().plusSeconds(1);

            assertThat(team.getCreatedAt()).isBetween(before, after);
            assertThat(team.getUpdatedAt()).isBetween(before, after);
        }

        @Test
        @DisplayName("does not overwrite an existing id")
        void onPersist_preservesExistingId() {
            UUID existing = UUID.randomUUID();
            Team team = new Team();
            team.setId(existing);
            team.onPersist();
            assertThat(team.getId()).isEqualTo(existing);
        }
    }

    // ══ @PreUpdate ════════════════════════════════════════════════

    @Test
    @DisplayName("onUpdate() refreshes updatedAt timestamp")
    void onUpdate_refreshesTimestamp() throws InterruptedException {
        Team team = buildTeam(1, 5, TeamStatus.OPEN);
        Instant original = team.getUpdatedAt();
        Thread.sleep(2); // ensure clock advances
        team.onUpdate();
        assertThat(team.getUpdatedAt()).isAfter(original);
    }
}