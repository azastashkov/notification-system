package com.notification.server.dto;

import com.notification.server.model.NotificationStatus;

public record SendNotificationResponse(
        Long notificationId,
        NotificationStatus status,
        String message
) {
}
