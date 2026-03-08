package org.notification.emulator.controller;

import org.notification.emulator.model.DeliveryRequest;
import org.notification.emulator.model.DeliveryResponse;
import org.notification.emulator.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/apns")
@RequiredArgsConstructor
public class ApnsController {

    private final DeliveryService deliveryService;

    @PostMapping("/send")
    public DeliveryResponse send(@RequestBody DeliveryRequest request) {
        return deliveryService.deliver("apns", request);
    }
}
