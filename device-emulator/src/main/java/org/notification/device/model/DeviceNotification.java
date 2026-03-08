package org.notification.device.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceNotification {

    private Long notificationId;
    private String channel;
    private String recipient;
    private String title;
    private String body;
    private LocalDateTime receivedAt;
}
