package com.techhub.teamservice.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
    public static ResourceNotFoundException team(Object id) {
        return new ResourceNotFoundException("Team not found with id: " + id);
    }
    public static ResourceNotFoundException invitation(Object id) {
        return new ResourceNotFoundException("Invitation not found with id: " + id);
    }
    public static ResourceNotFoundException member(Object id) {
        return new ResourceNotFoundException("Team member not found: " + id);
    }
}

