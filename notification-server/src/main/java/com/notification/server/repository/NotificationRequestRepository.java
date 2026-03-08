package com.notification.server.repository;

import com.notification.server.model.NotificationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRequestRepository extends JpaRepository<NotificationRequest, Long> {
}
