package com.techhub.teamservice.service.impl;

import com.techhub.teamservice.dto.InvitationRequest;
import com.techhub.teamservice.dto.InvitationResponse;
import com.techhub.teamservice.entity.Team;
import com.techhub.teamservice.entity.TeamInvitation;
import com.techhub.teamservice.entity.TeamMember;
import com.techhub.teamservice.entity.enums.InvitationStatus;
import com.techhub.teamservice.entity.enums.MemberRole;
import com.techhub.teamservice.exception.DuplicateInvitationException;
import com.techhub.teamservice.exception.InvitationExpiredException;
import com.techhub.teamservice.exception.ResourceNotFoundException;
import com.techhub.teamservice.exception.TeamFullException;
import com.techhub.teamservice.mapper.InvitationMapper;
import com.techhub.teamservice.repository.InvitationRepository;
import com.techhub.teamservice.repository.TeamMemberRepository;
import com.techhub.teamservice.repository.TeamRepository;
import com.techhub.teamservice.event.TeamEventProducer;
import com.techhub.teamservice.service.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import com.techhub.teamservice.entity.enums.TeamStatus;

@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {

    private final InvitationRepository invitationRepository;
    private final InvitationMapper invitationMapper;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamEventProducer teamEventProducer;

    @Value("${app.invitation.expiry-hours:48}")
    private int expiryHours;

    @Override
    public InvitationResponse invite(InvitationRequest request, UUID senderId) {
        Team team = teamRepository.findById(request.teamId())
                .orElseThrow(() -> ResourceNotFoundException.team(request.teamId()));

        if (team.getCurrentMembers() >= team.getMaxMembers()) {
            throw new TeamFullException(team.getId());
        }

        invitationRepository.findByTeamIdAndReceiverIdAndStatus(
                        request.teamId(), request.receiverId(), InvitationStatus.PENDING)
                .ifPresent(i -> { throw new DuplicateInvitationException(request.receiverId(), request.teamId()); });

        Instant now = Instant.now();

        TeamInvitation entity = TeamInvitation.builder()
                .id(UUID.randomUUID())
                .teamId(request.teamId())
                .senderId(senderId)
                .receiverId(request.receiverId())
                .status(InvitationStatus.PENDING)
                .expirationTime(now.plus(expiryHours, ChronoUnit.HOURS))
                .createdAt(now)
                .updatedAt(now)
                .build();

        TeamInvitation saved = invitationRepository.save(entity);

        // publish event stub (topic wiring handled in TeamEventProducer)
        teamEventProducer.sendTeamCreated(Map.of("event","team-invited","invitationId", saved.getId()));

        return invitationMapper.toResponse(saved);
    }

    @Override
    public InvitationResponse accept(UUID invitationId, UUID receiverId) {
        TeamInvitation inv = invitationRepository.findById(invitationId)
                .orElseThrow(() -> ResourceNotFoundException.invitation(invitationId));

        if (!inv.getReceiverId().equals(receiverId)) {
            throw new RuntimeException("Receiver mismatch");
        }

        Instant now = Instant.now();
        if (inv.getExpirationTime().isBefore(now)) {
            inv.setStatus(InvitationStatus.EXPIRED);
            invitationRepository.save(inv);
            throw new InvitationExpiredException(invitationId);
        }

        Team team = teamRepository.findById(inv.getTeamId())
                .orElseThrow(() -> ResourceNotFoundException.team(inv.getTeamId()));

        if (team.getCurrentMembers() >= team.getMaxMembers()) {
            throw new TeamFullException(team.getId());
        }

        // add member
        if (teamMemberRepository.existsByTeamIdAndUserId(team.getId(), receiverId)) {
            // already a member; mark invitation accepted anyway
            inv.setStatus(InvitationStatus.ACCEPTED);
            invitationRepository.save(inv);
            return invitationMapper.toResponse(inv);
        }

        TeamMember member = TeamMember.builder()
                .id(UUID.randomUUID())
                .teamId(team.getId())
                .userId(receiverId)
                .role(MemberRole.MEMBER)
                .joinedAt(now)
                .build();

        teamMemberRepository.save(member);

        // update team counts/status
        team.setCurrentMembers(team.getCurrentMembers() + 1);
        if (team.getCurrentMembers() >= team.getMaxMembers()) {
            team.setStatus(TeamStatus.FULL);
        }
        team.setUpdatedAt(now);
        teamRepository.save(team);

        inv.setStatus(InvitationStatus.ACCEPTED);
        inv.setUpdatedAt(now);
        invitationRepository.save(inv);

        // publish event stub
        teamEventProducer.sendTeamCreated(Map.of("event","team-joined","teamId", team.getId(), "userId", receiverId));

        return invitationMapper.toResponse(inv);
    }

    @Override
    public void expirePendingInvitations() {
        Instant now = Instant.now();
        List<TeamInvitation> expired = invitationRepository.findAllByStatusAndExpirationTimeLessThanEqual(InvitationStatus.PENDING, now);
        if (!expired.isEmpty()) {
            expired.forEach(i -> {
                i.setStatus(InvitationStatus.EXPIRED);
                i.setUpdatedAt(now);
            });
            invitationRepository.saveAll(expired);
        }
    }

    @Override
    public List<InvitationResponse> listPendingForUser(UUID userId) {
        List<TeamInvitation> list = invitationRepository.findAllByReceiverIdAndStatus(userId, InvitationStatus.PENDING);
        return list.stream().map(invitationMapper::toResponse).toList();
    }
}

