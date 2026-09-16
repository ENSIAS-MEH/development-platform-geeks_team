package com.techhub.teamservice.service;

import com.techhub.teamservice.dto.InvitationRequest;
import com.techhub.teamservice.dto.InvitationResponse;

import java.util.List;
import java.util.UUID;

public interface InvitationService {

    InvitationResponse invite(InvitationRequest request, UUID senderId);

    InvitationResponse accept(UUID invitationId, UUID receiverId);

    void expirePendingInvitations();

    List<InvitationResponse> listPendingForUser(UUID userId);
}

