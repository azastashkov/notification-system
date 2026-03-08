package com.notification.emulator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
