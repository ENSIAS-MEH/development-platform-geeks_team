package com.devconnect.eventservice.controller;

import com.devconnect.eventservice.dto.*;
import com.devconnect.eventservice.enums.*;
import com.devconnect.eventservice.service.EventService;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * REST controller for event management endpoints.
 * Authentication is enforced by the API Gateway; this controller reads X-User-Id header.
 */
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Event management: create, publish, register, search")
public class EventController {

    private final EventService eventService;

    @PostMapping
    @Operation(
        summary = "Create a new event",
        description = "Creates a hackathon, conference, workshop, competition, or meetup. Starts in DRAFT status. Must be published separately.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "201", description = "Event created successfully")
    @ApiResponse(responseCode = "400", description = "Validation error — check details field")
    @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody CreateEventRequest request,
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(request, userId));
    }

    @GetMapping
    @Operation(summary = "Search/list events with filters")
    @ApiResponse(responseCode = "200", description = "Paginated list of events")
    public ResponseEntity<Page<EventResponse>> searchEvents(
            @Parameter(description = "Filter by event type") @RequestParam(required = false) EventType type,
            @Parameter(description = "Filter by event status") @RequestParam(required = false) EventStatus status,
            @Parameter(description = "Filter by organizer UUID") @RequestParam(required = false) UUID organizerId,
            @Parameter(description = "Search keyword in title") @RequestParam(required = false) String keyword,
            @Parameter(description = "Start date lower bound (ISO 8601)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @Parameter(description = "Start date upper bound (ISO 8601)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = "X-User-Id", required = false) UUID userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").ascending());
        return ResponseEntity.ok(eventService.searchEvents(type, status, organizerId, keyword, dateFrom, dateTo, userId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID")
    @ApiResponse(responseCode = "200", description = "Event found")
    @ApiResponse(responseCode = "404", description = "Event not found")
    public ResponseEntity<EventResponse> getEvent(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Id", required = false) UUID userId) {
        return ResponseEntity.ok(eventService.getEventById(id, userId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update event (organizer only)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Event updated")
    @ApiResponse(responseCode = "403", description = "Not the organizer")
    @ApiResponse(responseCode = "404", description = "Event not found")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEventRequest request,
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(eventService.updateEvent(id, request, userId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete event (organizer only)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "204", description = "Event deleted")
    @ApiResponse(responseCode = "403", description = "Not the organizer")
    @ApiResponse(responseCode = "404", description = "Event not found")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {
        eventService.deleteEvent(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "Publish a DRAFT event (organizer only)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Event published")
    @ApiResponse(responseCode = "403", description = "Not the organizer")
    @ApiResponse(responseCode = "404", description = "Event not found")
    public ResponseEntity<EventResponse> publishEvent(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(eventService.publishEvent(id, userId));
    }

    @PostMapping("/{id}/register")
    @Operation(summary = "Register current user for an event", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "201", description = "Registered successfully")
    @ApiResponse(responseCode = "400", description = "Event not published")
    @ApiResponse(responseCode = "409", description = "Already registered or event full")
    @ApiResponse(responseCode = "404", description = "Event not found")
    public ResponseEntity<RegistrationResponse> register(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.registerForEvent(id, userId));
    }

    @DeleteMapping("/{id}/register")
    @Operation(summary = "Cancel current user registration", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "204", description = "Registration cancelled")
    @ApiResponse(responseCode = "404", description = "Registration not found")
    public ResponseEntity<Void> cancelRegistration(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {
        eventService.cancelRegistration(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/participants")
    @Operation(summary = "List confirmed participants for an event")
    @ApiResponse(responseCode = "200", description = "Paginated participant list")
    @ApiResponse(responseCode = "404", description = "Event not found")
    public ResponseEntity<Page<RegistrationResponse>> getParticipants(
            @PathVariable UUID id,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(eventService.getParticipants(id, PageRequest.of(page, size)));
    }
}
