package com.techhub.teamservice.exception;

public class InvitationExpiredException extends RuntimeException {
    public InvitationExpiredException(Object invitationId) {
        super("Invitation " + invitationId + " has expired and can no longer be accepted");
    }
}

