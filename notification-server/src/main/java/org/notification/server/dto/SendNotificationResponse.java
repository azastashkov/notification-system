package org.notification.server.dto;

import org.notification.server.model.NotificationStatus;

public record SendNotificationResponse(
        Long notificationId,
        NotificationStatus status,
        String message
) {
}
