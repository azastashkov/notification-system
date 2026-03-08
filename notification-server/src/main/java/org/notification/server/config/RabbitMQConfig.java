package org.notification.server.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "notification.exchange";
    public static final String QUEUE_APNS = "notification.apns";
    public static final String QUEUE_FCM = "notification.fcm";
    public static final String QUEUE_SMS = "notification.sms";
    public static final String QUEUE_EMAIL = "notification.email";

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue apnsQueue() {
        return QueueBuilder.durable(QUEUE_APNS).build();
    }

    @Bean
    public Queue fcmQueue() {
        return QueueBuilder.durable(QUEUE_FCM).build();
    }

    @Bean
    public Queue smsQueue() {
        return QueueBuilder.durable(QUEUE_SMS).build();
    }

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(QUEUE_EMAIL).build();
    }

    @Bean
    public Binding apnsBinding(Queue apnsQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(apnsQueue).to(notificationExchange).with(QUEUE_APNS);
    }

    @Bean
    public Binding fcmBinding(Queue fcmQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(fcmQueue).to(notificationExchange).with(QUEUE_FCM);
    }

    @Bean
    public Binding smsBinding(Queue smsQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(smsQueue).to(notificationExchange).with(QUEUE_SMS);
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(emailQueue).to(notificationExchange).with(QUEUE_EMAIL);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
