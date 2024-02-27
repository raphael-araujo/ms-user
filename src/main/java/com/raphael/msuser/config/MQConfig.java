package com.raphael.msuser.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {

    @Value("${mq.queues.ms-notification}")
    private String notificationQueue;

    @Bean
    public Queue queueNotification() {
        return new Queue(notificationQueue, true);
    }
}
