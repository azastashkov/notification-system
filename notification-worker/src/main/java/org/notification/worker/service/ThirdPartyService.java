package org.notification.worker.service;

import org.notification.worker.consumer.NotificationEvent;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class ThirdPartyService {

    private final RestTemplate restTemplate;
    private final MeterRegistry meterRegistry;
    private final String baseUrl;

    public ThirdPartyService(RestTemplate restTemplate, MeterRegistry meterRegistry,
                             @Value("${thirdparty.base-url:http://localhost:8082}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.meterRegistry = meterRegistry;
        this.baseUrl = baseUrl;
    }

    public boolean send(String channel, NotificationEvent event) {
        String url = baseUrl + "/api/" + channel + "/send";
        Map<String, Object> payload = Map.of(
                "notificationId", event.getNotificationId(),
                "recipient", event.getRecipient(),
                "title", event.getTitle(),
                "body", event.getBody()
        );

        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                restTemplate.postForObject(url, payload, Map.class);
                Counter.builder("notifications.sent")
                        .tag("channel", channel)
                        .tag("status", "success")
                        .register(meterRegistry).increment();
                log.info("Successfully sent {} notification {} (attempt {})", channel, event.getNotificationId(), attempt);
                return true;
            } catch (Exception e) {
                log.warn("Failed to send {} notification {} (attempt {}): {}", channel, event.getNotificationId(), attempt, e.getMessage());
                if (attempt < 3) {
                    try {
                        Thread.sleep(1000L * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        Counter.builder("notifications.sent")
                .tag("channel", channel)
                .tag("status", "failed")
                .register(meterRegistry).increment();
        log.error("Failed to send {} notification {} after 3 attempts", channel, event.getNotificationId());
        return false;
    }
}
