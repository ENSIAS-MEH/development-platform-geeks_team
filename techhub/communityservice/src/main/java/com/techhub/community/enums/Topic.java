package com.techhub.community.enums;

/**
 * Allowed topics for community groups.
 * Stored as VARCHAR(40) in the database, validated via CHECK constraint.
 */
public enum Topic {
    WEB,
    MOBILE,
    AI_ML,
    DEVOPS,
    SECURITY,
    GAME_DEV,
    DATA,
    OTHER
}
