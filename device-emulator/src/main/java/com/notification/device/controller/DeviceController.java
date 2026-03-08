package com.notification.device.controller;

import com.notification.device.model.DeviceNotification;
import com.notification.device.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping("/deliver")
    public void deliver(@RequestBody DeviceNotification notification) {
        deviceService.receive(notification);
    }

    @GetMapping("/notifications")
    public List<DeviceNotification> getNotifications(@RequestParam(required = false) String recipient) {
        if (recipient != null) {
            return deviceService.getNotificationsByRecipient(recipient);
        }
        return deviceService.getNotifications();
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return deviceService.getStats();
    }
}
