package org.notification.worker.consumer;

import org.notification.worker.service.ThirdPartyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApnsConsumer {

    private final ThirdPartyService thirdPartyService;

    @RabbitListener(queues = "notification.apns")
    public void handleMessage(NotificationEvent event) {
        log.info("Received APNS notification: {}", event.getNotificationId());
        thirdPartyService.send("apns", event);
    }
}
