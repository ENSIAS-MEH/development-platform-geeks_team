package com.devconnect.eventservice.exception;

import java.util.UUID;

public class EventNotPublishedException extends RuntimeException {
    public EventNotPublishedException(UUID eventId) {
        super("Event " + eventId + " is not published");
    }
}
