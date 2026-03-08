package com.notification.server.repository;

import com.notification.server.model.NotificationTemplate;
import com.notification.server.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {

    Optional<NotificationTemplate> findByNameAndType(String name, NotificationType type);
}
