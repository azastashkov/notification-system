package com.notification.server.dto;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEvent implements Serializable {

    private Long notificationId;
    private Long userId;
    private String type;
    private String title;
    private String body;
    private String recipient;
}
