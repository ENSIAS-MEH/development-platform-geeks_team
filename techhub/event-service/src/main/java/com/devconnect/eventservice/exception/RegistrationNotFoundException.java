package com.devconnect.eventservice.exception;

import java.util.UUID;

public class RegistrationNotFoundException extends RuntimeException {
    public RegistrationNotFoundException(UUID eventId, UUID userId) {
        super("Registration not found for event " + eventId + " user " + userId);
    }
}
