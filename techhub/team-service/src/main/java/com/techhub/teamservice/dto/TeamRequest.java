package com.techhub.teamservice.dto;

import jakarta.validation.constraints.*;

/**
 * Inbound DTO for team creation and update requests.
 *
 * <p>Java record — immutable, no boilerplate, compact constructor
 * runs field-level validation before the record is constructed.
 *
 * <p>All constraints are validated by {@code @Valid} on the
 * controller {@code @RequestBody} parameter. Validation failures
 * are caught by {@code GlobalExceptionHandler} and returned as a
 * structured {@code fieldErrors} map with HTTP 400.
 */
public record TeamRequest(

        @NotBlank(message = "Team name is required")
        @Size(min = 3, max = 120,
              message = "Team name must be between 3 and 120 characters")
        String name,

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description,

        @NotNull(message = "Maximum members is required")
        @Min(value = 2,   message = "Team must allow at least 2 members")
        @Max(value = 100, message = "Team cannot exceed 100 members")
        Integer maxMembers

) {
    /**
     * Compact constructor — trims whitespace from string fields
     * before any service logic runs. Prevents " My Team " and "My Team"
     * from being treated as different teams.
     */
    public TeamRequest {
        if (name != null)        name = name.strip();
        if (description != null) description = description.strip();
    }
}
