package com.techhub.teamservice.repository;

import com.techhub.teamservice.entity.TeamInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InvitationRepository extends JpaRepository<TeamInvitation, UUID> {
}

