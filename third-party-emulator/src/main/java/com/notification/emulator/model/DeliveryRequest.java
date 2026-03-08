package com.notification.emulator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequest {

    private Long notificationId;
    private String recipient;
    private String title;
    private String body;
}
