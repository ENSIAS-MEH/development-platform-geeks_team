package com.devconnect.eventservice.exception;

import java.util.UUID;

public class EventFullException extends RuntimeException {
    public EventFullException(UUID eventId) {
        super("Event " + eventId + " has reached maximum participants");
    }
}
