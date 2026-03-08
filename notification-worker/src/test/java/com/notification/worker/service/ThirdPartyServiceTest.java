package com.notification.worker.service;

import com.notification.worker.consumer.NotificationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThirdPartyServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private MeterRegistry meterRegistry = new SimpleMeterRegistry();

    private ThirdPartyService thirdPartyService;

    @BeforeEach
    void setUp() {
        thirdPartyService = new ThirdPartyService(restTemplate, meterRegistry, "http://localhost:8082");
    }

    @Test
    void send_success_returnsTrue() {
        NotificationEvent event = NotificationEvent.builder()
                .notificationId(1L).userId(1L).type("EMAIL")
                .title("Test").body("Body").recipient("test@example.com").build();
        when(restTemplate.postForObject(anyString(), any(), eq(Map.class)))
                .thenReturn(Map.of("success", true));

        boolean result = thirdPartyService.send("email", event);

        assertTrue(result);
    }

    @Test
    void send_failure_returnsFalse() {
        NotificationEvent event = NotificationEvent.builder()
                .notificationId(1L).userId(1L).type("EMAIL")
                .title("Test").body("Body").recipient("test@example.com").build();
        when(restTemplate.postForObject(anyString(), any(), eq(Map.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        boolean result = thirdPartyService.send("email", event);

        assertFalse(result);
    }
}
