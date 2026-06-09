package com.techhub.teamservice.exception;

import lombok.Builder;

import java.time.Instant;
import java.util.Map;

/**
 * Standard API error response body.
 * Builder pattern ensures immutability and readability at construction sites.
 */
@Builder
public record ErrorResponse(
        int     status,
        String  error,
        String  message,
        String  path,
        Instant timestamp
) {}

