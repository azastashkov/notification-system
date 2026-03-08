package org.notification.emulator;

import org.notification.emulator.model.DeliveryRequest;
import org.notification.emulator.model.DeliveryResponse;
import org.notification.emulator.service.DeliveryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Autowired
    private DeliveryService deliveryService;

    @MockitoBean
    private RestTemplate restTemplate;

    @Test
    void deliver_returnsResponse() {
        DeliveryRequest request = new DeliveryRequest(1L, "test@example.com", "Title", "Body");
        DeliveryResponse response = deliveryService.deliver("email", request);

        assertNotNull(response);
        assertNotNull(response.getDeliveryId());
        assertEquals(1L, response.getNotificationId());
    }

    @Test
    void deliver_generatesUniqueDeliveryIds() {
        DeliveryRequest request = new DeliveryRequest(1L, "test@example.com", "Title", "Body");
        DeliveryResponse r1 = deliveryService.deliver("email", request);
        DeliveryResponse r2 = deliveryService.deliver("email", request);

        assertNotEquals(r1.getDeliveryId(), r2.getDeliveryId());
    }
}
