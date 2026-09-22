package com.techhub.community.repository;

import com.techhub.community.entity.GroupMember;
import com.techhub.community.enums.MemberRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, UUID> {

    /** Check if a user is already a member of a group */
    boolean existsByGroupIdAndUserId(UUID groupId, UUID userId);

    /** Find a specific membership */
    Optional<GroupMember> findByGroupIdAndUserId(UUID groupId, UUID userId);

    /** All members of a group (paginated) */
    Page<GroupMember> findByGroupId(UUID groupId, Pageable pageable);

    /** All groups a user belongs to */
    List<GroupMember> findByUserId(UUID userId);

    /** Find a member's role in a group */
    Optional<GroupMember> findByGroupIdAndUserIdAndRole(UUID groupId, UUID userId, MemberRole role);

    /** Delete a membership */
    void deleteByGroupIdAndUserId(UUID groupId, UUID userId);

    /** Count members in a group */
    long countByGroupId(UUID groupId);
}
