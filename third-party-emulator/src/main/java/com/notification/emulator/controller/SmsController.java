package com.notification.emulator.controller;

import com.notification.emulator.model.DeliveryRequest;
import com.notification.emulator.model.DeliveryResponse;
import com.notification.emulator.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
public class SmsController {

    private final DeliveryService deliveryService;

    @PostMapping("/send")
    public DeliveryResponse send(@RequestBody DeliveryRequest request) {
        return deliveryService.deliver("sms", request);
    }
}
