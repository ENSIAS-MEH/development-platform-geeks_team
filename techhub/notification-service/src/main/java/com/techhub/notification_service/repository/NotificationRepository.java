package com.techhub.notification_service.repository;

import com.techhub.notification_service.entity.Notification;
import com.techhub.notification_service.entity.NotificationStatus;
import com.techhub.notification_service.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByUserId(UUID userId);

    List<Notification> findByStatus(NotificationStatus status);

    List<Notification> findByUserIdAndType(UUID userId, NotificationType type);

    // Idempotency guard — avoid sending the same event twice
    boolean existsByEventId(String eventId);
}