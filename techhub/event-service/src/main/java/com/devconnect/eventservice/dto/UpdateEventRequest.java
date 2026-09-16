package com.devconnect.eventservice.dto;

import com.devconnect.eventservice.enums.EventType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/** Request body for partial update of an event. All fields optional. */
@Data
public class UpdateEventRequest {

    @Size(min = 3, max = 200)
    private String title;

    private String description;

    private EventType type;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String location;

    @Min(2) @Max(10000)
    private Integer maxParticipants;

    private Set<String> tags;
}
