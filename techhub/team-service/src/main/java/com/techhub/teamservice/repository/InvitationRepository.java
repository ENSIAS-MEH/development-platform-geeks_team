package com.techhub.teamservice.repository;

import com.techhub.teamservice.entity.TeamInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import com.techhub.teamservice.entity.enums.InvitationStatus;

@Repository
public interface InvitationRepository extends JpaRepository<TeamInvitation, UUID> {

	Optional<TeamInvitation> findByTeamIdAndReceiverIdAndStatus(UUID teamId, UUID receiverId, InvitationStatus status);

	List<TeamInvitation> findAllByReceiverIdAndStatus(UUID receiverId, InvitationStatus status);

	List<TeamInvitation> findAllByStatusAndExpirationTimeLessThanEqual(InvitationStatus status, Instant time);
}

