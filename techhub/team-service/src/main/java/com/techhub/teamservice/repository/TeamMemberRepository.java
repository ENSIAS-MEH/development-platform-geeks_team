package com.techhub.teamservice.repository;

import com.techhub.teamservice.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, UUID> {

	List<TeamMember> findAllByTeamId(UUID teamId);

	Optional<TeamMember> findByTeamIdAndUserId(UUID teamId, UUID userId);

	boolean existsByTeamIdAndUserId(UUID teamId, UUID userId);
}

