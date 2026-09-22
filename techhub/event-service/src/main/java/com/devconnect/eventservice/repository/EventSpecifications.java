package com.devconnect.eventservice.repository;

import com.devconnect.eventservice.entity.Event;
import com.devconnect.eventservice.enums.EventStatus;
import com.devconnect.eventservice.enums.EventType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;

/** Dynamic JPA specifications for event search (avoids PostgreSQL null-parameter bugs). */
public final class EventSpecifications {

    private EventSpecifications() {}

    public static Specification<Event> withType(EventType type) {
        return (root, query, cb) -> type == null ? cb.conjunction() : cb.equal(root.get("type"), type);
    }

    public static Specification<Event> withStatus(EventStatus status) {
        return (root, query, cb) -> status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<Event> withOrganizerId(UUID organizerId) {
        return (root, query, cb) ->
            organizerId == null ? cb.conjunction() : cb.equal(root.get("organizerId"), organizerId);
    }

    public static Specification<Event> withKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.like(cb.lower(root.get("title")), pattern);
        };
    }

    public static Specification<Event> withStartDateFrom(LocalDateTime dateFrom) {
        return (root, query, cb) ->
            dateFrom == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("startDate"), dateFrom);
    }

    public static Specification<Event> withStartDateTo(LocalDateTime dateTo) {
        return (root, query, cb) ->
            dateTo == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("startDate"), dateTo);
    }
}
