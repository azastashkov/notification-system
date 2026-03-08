package org.notification.server.service;

import org.notification.server.dto.NotificationEvent;
import org.notification.server.model.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(NotificationType type, NotificationEvent event) {
        String routingKey = "notification." + type.name().toLowerCase();
        log.info("Publishing notification {} to queue {}", event.getNotificationId(), routingKey);
        rabbitTemplate.convertAndSend("notification.exchange", routingKey, event);
    }
}
