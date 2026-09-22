package com.devconnect.eventservice.exception;

import java.util.UUID;

public class AlreadyRegisteredException extends RuntimeException {
    public AlreadyRegisteredException(UUID eventId, UUID userId) {
        super("User " + userId + " is already registered for event " + eventId);
    }
}
