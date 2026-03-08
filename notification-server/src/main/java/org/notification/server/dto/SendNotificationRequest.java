package org.notification.server.dto;

import org.notification.server.model.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record SendNotificationRequest(
        @NotNull Long userId,
        @NotNull NotificationType type,
        String title,
        String body,
        String templateName,
        Map<String, String> templateParams
) {
}
