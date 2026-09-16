package com.techhub.teamservice.exception;

public class DuplicateInvitationException extends RuntimeException {
    public DuplicateInvitationException(Object userId, Object teamId) {
        super("User " + userId + " already has a pending invitation for team " + teamId);
    }
}

