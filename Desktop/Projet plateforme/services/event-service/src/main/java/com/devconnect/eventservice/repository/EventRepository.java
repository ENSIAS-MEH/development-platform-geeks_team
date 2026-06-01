package com.devconnect.eventservice.repository;

import com.devconnect.eventservice.entity.Event;
import com.devconnect.eventservice.enums.EventStatus;
import com.devconnect.eventservice.enums.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for Event entity with custom query methods.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    Page<Event> findByStatus(EventStatus status, Pageable pageable);

    Page<Event> findByTypeAndStatus(EventType type, EventStatus status, Pageable pageable);

    List<Event> findByOrganizerId(UUID organizerId);

    Page<Event> findByStartDateAfter(LocalDateTime date, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Event> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT e FROM Event e JOIN e.tags t WHERE t = :tag")
    Page<Event> findByTag(@Param("tag") String tag, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE " +
           "(:type IS NULL OR e.type = :type) AND " +
           "(:status IS NULL OR e.status = :status) AND " +
           "(:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:dateFrom IS NULL OR e.startDate >= :dateFrom) AND " +
           "(:dateTo IS NULL OR e.startDate <= :dateTo)")
    Page<Event> searchEvents(
        @Param("type") EventType type,
        @Param("status") EventStatus status,
        @Param("keyword") String keyword,
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo,
        Pageable pageable
    );
}
