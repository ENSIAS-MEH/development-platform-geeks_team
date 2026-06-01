package com.techhub.teamservice.service.impl;

import com.techhub.teamservice.dto.InvitationRequest;
import com.techhub.teamservice.dto.InvitationResponse;
import com.techhub.teamservice.mapper.InvitationMapper;
import com.techhub.teamservice.repository.InvitationRepository;
import com.techhub.teamservice.service.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {

    private final InvitationRepository invitationRepository;
    private final InvitationMapper invitationMapper;

    @Override
    public InvitationResponse invite(InvitationRequest request, UUID senderId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public InvitationResponse accept(UUID invitationId, UUID receiverId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void expirePendingInvitations() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<InvitationResponse> listPendingForUser(UUID userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

