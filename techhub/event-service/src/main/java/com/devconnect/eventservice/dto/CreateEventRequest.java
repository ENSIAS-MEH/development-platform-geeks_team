package com.devconnect.eventservice.dto;

import com.devconnect.eventservice.enums.EventType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Request body for creating a new event.
 * Dates are validated in service layer (endDate > startDate).
 */
@Data
public class CreateEventRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    private String description;

    @NotNull(message = "Event type is required")
    private EventType type;

    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    private String location;

    @Min(value = 2, message = "Minimum 2 participants required")
    @Max(value = 10000, message = "Maximum 10000 participants allowed")
    private Integer maxParticipants;

    private Set<String> tags;
}
