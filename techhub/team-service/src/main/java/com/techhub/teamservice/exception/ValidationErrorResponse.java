package com.techhub.teamservice.exception;

import lombok.Builder;

import java.time.Instant;
import java.util.Map;

/**
 * Extended error response for @Valid failures — adds per-field error map.
 */
@Builder
public record ValidationErrorResponse(
        int                   status,
        String                error,
        String                message,
        String                path,
        Instant               timestamp,
        Map<String, String>   fieldErrors
) {}

