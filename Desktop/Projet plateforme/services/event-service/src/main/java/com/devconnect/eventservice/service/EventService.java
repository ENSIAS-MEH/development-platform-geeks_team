package com.devconnect.eventservice.service;

import com.devconnect.eventservice.dto.*;
import com.devconnect.eventservice.entity.*;
import com.devconnect.eventservice.enums.*;
import com.devconnect.eventservice.exception.*;
import com.devconnect.eventservice.kafka.EventProducer;
import com.devconnect.eventservice.kafka.event.*;
import com.devconnect.eventservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Business logic for events and registrations.
 * Handles date validation, capacity checks, ownership enforcement, caching, and Kafka publishing.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = "events")
public class EventService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final EventProducer eventProducer;

    /**
     * Creates a new event in DRAFT status and publishes an event-created Kafka message.
     *
     * @param request the event creation data
     * @param organizerId the UUID of the authenticated organizer
     * @return the created event response
     * @throws InvalidEventDatesException if endDate is not after startDate
     */
    @Transactional
    @CacheEvict(cacheNames = "event-list", allEntries = true)
    public EventResponse createEvent(CreateEventRequest request, UUID organizerId) {
        if (request.getEndDate() != null && request.getStartDate() != null
            && !request.getEndDate().isAfter(request.getStartDate())) {
            throw new InvalidEventDatesException();
        }

        Event event = Event.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .type(request.getType())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .location(request.getLocation())
            .maxParticipants(request.getMaxParticipants())
            .organizerId(organizerId)
            .status(EventStatus.DRAFT)
            .tags(request.getTags() != null ? request.getTags() : new java.util.HashSet<>())
            .build();

        Event saved = eventRepository.save(event);

        eventProducer.publishEventCreated(EventCreatedEvent.builder()
            .id(saved.getId())
            .title(saved.getTitle())
            .organizerId(saved.getOrganizerId())
            .type(saved.getType().name())
            .startDate(saved.getStartDate())
            .build());

        return toResponse(saved, 0, false);
    }

    /**
     * Publishes a DRAFT event, changing its status to PUBLISHED.
     *
     * @param eventId the event UUID
     * @param requesterId the authenticated user UUID
     * @return the published event response
     * @throws EventNotFoundException if the event does not exist
     * @throws UnauthorizedActionException if the requester is not the organizer
     */
    @Transactional
    @CacheEvict(value = "events", key = "#eventId")
    public EventResponse publishEvent(UUID eventId, UUID requesterId) {
        Event event = findByIdOrThrow(eventId);
        if (!event.getOrganizerId().equals(requesterId)) {
            throw new UnauthorizedActionException("Only the organizer can publish this event");
        }
        event.setStatus(EventStatus.PUBLISHED);
        Event saved = eventRepository.save(event);

        eventProducer.publishEventPublished(EventPublishedEvent.builder()
            .eventId(saved.getId())
            .title(saved.getTitle())
            .organizerId(saved.getOrganizerId())
            .build());

        long count = registrationRepository.countByEventIdAndStatus(eventId, RegistrationStatus.CONFIRMED);
        return toResponse(saved, count, false);
    }

    /**
     * Returns a single event by ID, with Redis caching.
     *
     * @param eventId the event UUID
     * @param currentUserId the authenticated user UUID (may be null)
     * @return the event response
     * @throws EventNotFoundException if not found
     */
    @Cacheable(key = "#eventId")
    public EventResponse getEventById(UUID eventId, UUID currentUserId) {
        Event event = findByIdOrThrow(eventId);
        long count = registrationRepository.countByEventIdAndStatus(eventId, RegistrationStatus.CONFIRMED);
        boolean registered = currentUserId != null && registrationRepository.existsByEventIdAndUserId(eventId, currentUserId);
        return toResponse(event, count, registered);
    }

    /**
     * Returns a paginated list of events with optional filters.
     *
     * @param type event type filter (nullable)
     * @param status event status filter (nullable)
     * @param keyword search keyword (nullable)
     * @param dateFrom start date lower bound (nullable)
     * @param dateTo start date upper bound (nullable)
     * @param pageable pagination parameters
     * @return paginated event responses
     */
    @Cacheable(cacheNames = "event-list", key = "#type + '_' + #status + '_' + #keyword + '_' + #pageable.pageNumber")
    public Page<EventResponse> searchEvents(EventType type, EventStatus status, String keyword,
                                            LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable) {
        return eventRepository.searchEvents(type, status, keyword, dateFrom, dateTo, pageable)
            .map(event -> {
                long count = registrationRepository.countByEventIdAndStatus(event.getId(), RegistrationStatus.CONFIRMED);
                return toResponse(event, count, false);
            });
    }

    /**
     * Registers the current user for a published event.
     * Validates: event is PUBLISHED, user not already registered, event not full.
     * Publishes a user-registered-event Kafka message.
     *
     * @param eventId the event UUID
     * @param userId the authenticated user UUID
     * @return the registration response
     * @throws EventNotFoundException if no event exists with the given ID
     * @throws EventNotPublishedException if the event is not in PUBLISHED status
     * @throws AlreadyRegisteredException if the user is already registered
     * @throws EventFullException if the event has reached its participant limit
     */
    @Transactional
    @CacheEvict(value = "events", key = "#eventId")
    public RegistrationResponse registerForEvent(UUID eventId, UUID userId) {
        Event event = findByIdOrThrow(eventId);

        if (event.getStatus() != EventStatus.PUBLISHED) {
            throw new EventNotPublishedException(eventId);
        }
        if (registrationRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new AlreadyRegisteredException(eventId, userId);
        }
        if (event.getMaxParticipants() != null) {
            long confirmed = registrationRepository.countByEventIdAndStatus(eventId, RegistrationStatus.CONFIRMED);
            if (confirmed >= event.getMaxParticipants()) {
                throw new EventFullException(eventId);
            }
        }

        Registration registration = Registration.builder()
            .eventId(eventId)
            .userId(userId)
            .status(RegistrationStatus.CONFIRMED)
            .build();

        Registration saved = registrationRepository.save(registration);

        eventProducer.publishUserRegistered(UserRegisteredEvent.builder()
            .userId(userId)
            .eventId(eventId)
            .eventTitle(event.getTitle())
            .organizerId(event.getOrganizerId())
            .build());

        return toRegistrationResponse(saved);
    }

    /**
     * Cancels a user's registration for an event.
     *
     * @param eventId the event UUID
     * @param userId the authenticated user UUID
     * @throws RegistrationNotFoundException if no active registration exists
     */
    @Transactional
    @CacheEvict(value = "events", key = "#eventId")
    public void cancelRegistration(UUID eventId, UUID userId) {
        Registration reg = registrationRepository.findByEventIdAndUserId(eventId, userId)
            .orElseThrow(() -> new RegistrationNotFoundException(eventId, userId));
        reg.setStatus(RegistrationStatus.CANCELLED);
        registrationRepository.save(reg);
    }

    /**
     * Returns a paginated list of confirmed participants for an event.
     *
     * @param eventId the event UUID
     * @param pageable pagination parameters
     * @return paginated registration responses
     * @throws EventNotFoundException if the event does not exist
     */
    public Page<RegistrationResponse> getParticipants(UUID eventId, Pageable pageable) {
        findByIdOrThrow(eventId);
        return registrationRepository.findByEventId(eventId, pageable)
            .map(this::toRegistrationResponse);
    }

    /**
     * Updates an existing event. Only the organizer can update.
     *
     * @param eventId the event UUID
     * @param request the partial update data
     * @param requesterId the authenticated user UUID
     * @return the updated event response
     * @throws EventNotFoundException if the event does not exist
     * @throws UnauthorizedActionException if the requester is not the organizer
     * @throws InvalidEventDatesException if end date is not after start date
     */
    @Transactional
    @CacheEvict(cacheNames = {"events", "event-list"}, allEntries = true)
    public EventResponse updateEvent(UUID eventId, UpdateEventRequest request, UUID requesterId) {
        Event event = findByIdOrThrow(eventId);
        if (!event.getOrganizerId().equals(requesterId)) {
            throw new UnauthorizedActionException("Only the organizer can update this event");
        }

        if (request.getTitle() != null) event.setTitle(request.getTitle());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getType() != null) event.setType(request.getType());
        if (request.getLocation() != null) event.setLocation(request.getLocation());
        if (request.getMaxParticipants() != null) event.setMaxParticipants(request.getMaxParticipants());
        if (request.getTags() != null) event.setTags(request.getTags());

        if (request.getStartDate() != null) event.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) event.setEndDate(request.getEndDate());
        if (event.getEndDate() != null && !event.getEndDate().isAfter(event.getStartDate())) {
            throw new InvalidEventDatesException();
        }

        Event saved = eventRepository.save(event);
        long count = registrationRepository.countByEventIdAndStatus(eventId, RegistrationStatus.CONFIRMED);
        return toResponse(saved, count, false);
    }

    /**
     * Deletes an event. Only the organizer can delete.
     *
     * @param eventId the event UUID
     * @param requesterId the authenticated user UUID
     * @throws EventNotFoundException if the event does not exist
     * @throws UnauthorizedActionException if the requester is not the organizer
     */
    @Transactional
    @CacheEvict(cacheNames = {"events", "event-list"}, allEntries = true)
    public void deleteEvent(UUID eventId, UUID requesterId) {
        Event event = findByIdOrThrow(eventId);
        if (!event.getOrganizerId().equals(requesterId)) {
            throw new UnauthorizedActionException("Only the organizer can delete this event");
        }
        eventRepository.delete(event);
    }

    private Event findByIdOrThrow(UUID id) {
        return eventRepository.findById(id)
            .orElseThrow(() -> new EventNotFoundException(id));
    }

    private EventResponse toResponse(Event event, long participantCount, boolean userRegistered) {
        return EventResponse.builder()
            .id(event.getId())
            .title(event.getTitle())
            .description(event.getDescription())
            .type(event.getType())
            .startDate(event.getStartDate())
            .endDate(event.getEndDate())
            .location(event.getLocation())
            .maxParticipants(event.getMaxParticipants())
            .organizerId(event.getOrganizerId())
            .status(event.getStatus())
            .tags(event.getTags())
            .participantCount(participantCount)
            .userRegistered(userRegistered)
            .createdAt(event.getCreatedAt())
            .updatedAt(event.getUpdatedAt())
            .build();
    }

    private RegistrationResponse toRegistrationResponse(Registration r) {
        return RegistrationResponse.builder()
            .id(r.getId())
            .eventId(r.getEventId())
            .userId(r.getUserId())
            .registeredAt(r.getRegisteredAt())
            .status(r.getStatus())
            .build();
    }
}
