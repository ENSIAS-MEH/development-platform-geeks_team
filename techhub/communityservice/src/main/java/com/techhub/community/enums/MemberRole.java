package com.techhub.community.enums;

/**
 * Roles that a member can have within a community group.
 * Stored as VARCHAR(20) in the database, validated via CHECK constraint.
 */
public enum MemberRole {
    OWNER,
    MODERATOR,
    MEMBER
}
