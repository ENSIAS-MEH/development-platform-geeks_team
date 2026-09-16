package com.techhub.teamservice.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) { super(message); }
    public static ForbiddenException notOwner() {
        return new ForbiddenException("Only the team owner can perform this action");
    }
    public static ForbiddenException notMember() {
        return new ForbiddenException("You are not a member of this team");
    }
}

