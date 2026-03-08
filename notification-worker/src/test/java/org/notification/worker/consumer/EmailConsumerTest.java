package org.notification.worker.consumer;

import org.notification.worker.service.ThirdPartyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailConsumerTest {

    @Mock
    private ThirdPartyService thirdPartyService;

    @InjectMocks
    private EmailConsumer emailConsumer;

    @Test
    void handleMessage_callsThirdPartyService() {
        NotificationEvent event = NotificationEvent.builder()
                .notificationId(1L).userId(1L).type("EMAIL")
                .title("Test").body("Test body").recipient("test@example.com").build();

        emailConsumer.handleMessage(event);

        verify(thirdPartyService).send("email", event);
    }
}
