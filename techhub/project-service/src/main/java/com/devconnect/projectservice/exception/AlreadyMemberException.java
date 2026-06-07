package com.devconnect.projectservice.exception;

import java.util.UUID;

public class AlreadyMemberException extends RuntimeException {
    public AlreadyMemberException(UUID projectId, UUID userId) {
        super("User " + userId + " is already a member of project " + projectId);
    }
}
