package com.techhub.teamservice.scheduler;

import com.techhub.teamservice.service.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InvitationExpirationScheduler {

    private final InvitationService invitationService;

    @Scheduled(fixedRateString = "${app.invitation.scheduler-rate-ms:60000}")
    public void expireInvitations() {
        // delegate to service — no logic in scheduler
        invitationService.expirePendingInvitations();
    }
}

