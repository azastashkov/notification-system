package org.notification.worker.consumer;

import org.notification.worker.service.ThirdPartyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmsConsumer {

    private final ThirdPartyService thirdPartyService;

    @RabbitListener(queues = "notification.sms")
    public void handleMessage(NotificationEvent event) {
        log.info("Received SMS notification: {}", event.getNotificationId());
        thirdPartyService.send("sms", event);
    }
}
