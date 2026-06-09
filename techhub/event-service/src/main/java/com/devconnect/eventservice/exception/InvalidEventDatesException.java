package com.devconnect.eventservice.exception;

public class InvalidEventDatesException extends RuntimeException {
    public InvalidEventDatesException() {
        super("End date must be after start date");
    }
}
