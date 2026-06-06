package com.techhub.teamservice.exception;

public class TeamFullException extends RuntimeException {
    public TeamFullException(Object teamId) {
        super("Team " + teamId + " has reached its maximum capacity and cannot accept new members");
    }
}

