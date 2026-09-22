package com.techhub.community.repository;

import com.techhub.community.entity.Group;
import com.techhub.community.enums.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {

    /** Browse public groups filtered by topic */
    Page<Group> findByTopicAndIsPublicTrue(Topic topic, Pageable pageable);

    /** Browse all public groups */
    Page<Group> findByIsPublicTrue(Pageable pageable);

    /** Groups created by a specific owner */
    List<Group> findByOwnerId(UUID ownerId);

    /** Search groups by name (case-insensitive) */
    Page<Group> findByNameContainingIgnoreCaseAndIsPublicTrue(String keyword, Pageable pageable);

    // ─── Atomic counter updates (race-condition safe) ───────────────────

    @Modifying
    @Query("UPDATE Group g SET g.memberCount = g.memberCount + 1 WHERE g.id = :groupId")
    void incrementMemberCount(@Param("groupId") UUID groupId);

    @Modifying
    @Query("UPDATE Group g SET g.memberCount = CASE WHEN g.memberCount > 0 THEN g.memberCount - 1 ELSE 0 END WHERE g.id = :groupId")
    void decrementMemberCount(@Param("groupId") UUID groupId);
}
