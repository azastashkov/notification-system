package com.notification.emulator.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequest {

    private Long notificationId;
    private String recipient;
    private String title;
    private String body;
}
