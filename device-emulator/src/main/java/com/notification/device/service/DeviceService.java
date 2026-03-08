package com.notification.device.service;

import com.notification.device.config.DeviceWebSocketHandler;
import com.notification.device.model.DeviceNotification;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DeviceService {

    private final ConcurrentLinkedQueue<DeviceNotification> notifications = new ConcurrentLinkedQueue<>();
    private final DeviceWebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper;

    private static final int MAX_NOTIFICATIONS = 1000;

    public DeviceService(DeviceWebSocketHandler webSocketHandler, ObjectMapper objectMapper) {
        this.webSocketHandler = webSocketHandler;
        this.objectMapper = objectMapper;
    }

    public void receive(DeviceNotification notification) {
        notification.setReceivedAt(LocalDateTime.now());
        notifications.add(notification);

        while (notifications.size() > MAX_NOTIFICATIONS) {
            notifications.poll();
        }

        log.info("Device received {} notification {} for {}",
                notification.getChannel(), notification.getNotificationId(), notification.getRecipient());

        try {
            webSocketHandler.broadcast(objectMapper.writeValueAsString(notification));
        } catch (Exception e) {
            log.warn("Failed to broadcast via WebSocket: {}", e.getMessage());
        }
    }

    public List<DeviceNotification> getNotifications() {
        return new ArrayList<>(notifications);
    }

    public List<DeviceNotification> getNotificationsByRecipient(String recipient) {
        return notifications.stream()
                .filter(n -> recipient.equals(n.getRecipient()))
                .collect(Collectors.toList());
    }

    public Map<String, Object> getStats() {
        Map<String, Long> byChannel = notifications.stream()
                .collect(Collectors.groupingBy(DeviceNotification::getChannel, Collectors.counting()));

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total", (long) notifications.size());
        stats.put("byChannel", byChannel);
        return stats;
    }
}
