package com.devconnect.projectservice.exception;

import java.util.UUID;

public class NotMemberException extends RuntimeException {
    public NotMemberException(UUID projectId, UUID userId) {
        super("User " + userId + " is not a member of project " + projectId);
    }
}
