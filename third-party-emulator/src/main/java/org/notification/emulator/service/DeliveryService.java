package org.notification.emulator.service;

import org.notification.emulator.model.DeliveryRequest;
import org.notification.emulator.model.DeliveryResponse;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class DeliveryService {

    private final RestTemplate restTemplate;
    private final MeterRegistry meterRegistry;
    private final String deviceEmulatorUrl;

    public DeliveryService(RestTemplate restTemplate, MeterRegistry meterRegistry,
                           @Value("${device-emulator.base-url:http://localhost:8083}") String deviceEmulatorUrl) {
        this.restTemplate = restTemplate;
        this.meterRegistry = meterRegistry;
        this.deviceEmulatorUrl = deviceEmulatorUrl;
    }

    public DeliveryResponse deliver(String channel, DeliveryRequest request) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextLong(50, 200));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean success = ThreadLocalRandom.current().nextDouble() < 0.95;
        String deliveryId = UUID.randomUUID().toString();

        Counter.builder("deliveries")
                .tag("channel", channel)
                .tag("status", success ? "success" : "failed")
                .register(meterRegistry).increment();

        if (success) {
            log.info("Delivered {} notification {} to {} (deliveryId: {})",
                    channel, request.getNotificationId(), request.getRecipient(), deliveryId);

            forwardToDeviceEmulator(channel, request);
        } else {
            log.warn("Failed to deliver {} notification {} to {}",
                    channel, request.getNotificationId(), request.getRecipient());
        }

        return DeliveryResponse.builder()
                .notificationId(request.getNotificationId())
                .success(success)
                .message(success ? "Delivered successfully" : "Delivery failed (simulated)")
                .deliveryId(deliveryId)
                .build();
    }

    private void forwardToDeviceEmulator(String channel, DeliveryRequest request) {
        try {
            Map<String, Object> payload = Map.of(
                    "notificationId", request.getNotificationId(),
                    "channel", channel,
                    "recipient", request.getRecipient(),
                    "title", request.getTitle(),
                    "body", request.getBody()
            );
            restTemplate.postForObject(deviceEmulatorUrl + "/api/devices/deliver", payload, Void.class);
        } catch (Exception e) {
            log.warn("Failed to forward to device emulator: {}", e.getMessage());
        }
    }
}
