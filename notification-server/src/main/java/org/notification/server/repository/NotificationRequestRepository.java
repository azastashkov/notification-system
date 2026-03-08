package org.notification.server.repository;

import org.notification.server.model.NotificationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRequestRepository extends JpaRepository<NotificationRequest, Long> {
}
