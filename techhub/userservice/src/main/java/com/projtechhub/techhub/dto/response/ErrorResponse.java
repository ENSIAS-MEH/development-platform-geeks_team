package com.projtechhub.techhub.dto.response;

import lombok.Builder;

import java.util.Map;

/**
 * @author pc
 **/
@Builder
public class ErrorResponse {

    private String timestamp;     // ISO string — Instant.now().toString()
    private int status;           // HTTP status code — 400, 401, 404 etc.
    private String message;       // human readable — "Invalid credentials"
    private Map<String, String> details;  // field-level errors from @Valid
    // e.g. {"email": "must be a valid email",
    //        "password": "size must be between 8 and..."}
    // null for non-validation errors
}
