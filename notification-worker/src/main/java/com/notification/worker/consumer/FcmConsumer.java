package com.notification.worker.consumer;

import com.notification.worker.service.ThirdPartyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FcmConsumer {

    private final ThirdPartyService thirdPartyService;

    @RabbitListener(queues = "notification.fcm")
    public void handleMessage(NotificationEvent event) {
        log.info("Received FCM notification: {}", event.getNotificationId());
        thirdPartyService.send("fcm", event);
    }
}
