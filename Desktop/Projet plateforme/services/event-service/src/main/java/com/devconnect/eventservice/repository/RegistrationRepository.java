package com.devconnect.eventservice.repository;

import com.devconnect.eventservice.entity.Registration;
import com.devconnect.eventservice.enums.RegistrationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Registration entity.
 */
@Repository
public interface RegistrationRepository extends JpaRepository<Registration, UUID> {

    Page<Registration> findByEventId(UUID eventId, Pageable pageable);

    List<Registration> findByUserId(UUID userId);

    long countByEventIdAndStatus(UUID eventId, RegistrationStatus status);

    boolean existsByEventIdAndUserId(UUID eventId, UUID userId);

    Optional<Registration> findByEventIdAndUserId(UUID eventId, UUID userId);
}
