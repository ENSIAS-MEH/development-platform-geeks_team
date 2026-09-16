package com.techhub.community.enums;

/**
 * Types of posts that can be created in a community group.
 * Stored as VARCHAR(30) in the database, validated via CHECK constraint.
 */
public enum PostType {
    DISCUSSION,
    ANNOUNCEMENT,
    RESOURCE
}
