package org.notification.server.repository;

import org.notification.server.model.NotificationTemplate;
import org.notification.server.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {

    Optional<NotificationTemplate> findByNameAndType(String name, NotificationType type);
}
