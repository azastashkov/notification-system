package com.notification.emulator.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryResponse {

    private Long notificationId;
    private boolean success;
    private String message;
    private String deliveryId;
}
