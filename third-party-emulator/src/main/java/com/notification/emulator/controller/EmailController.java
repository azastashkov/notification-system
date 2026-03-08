package com.notification.emulator.controller;

import com.notification.emulator.model.DeliveryRequest;
import com.notification.emulator.model.DeliveryResponse;
import com.notification.emulator.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final DeliveryService deliveryService;

    @PostMapping("/send")
    public DeliveryResponse send(@RequestBody DeliveryRequest request) {
        return deliveryService.deliver("email", request);
    }
}
