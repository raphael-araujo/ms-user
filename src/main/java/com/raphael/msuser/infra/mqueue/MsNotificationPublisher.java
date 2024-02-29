package com.raphael.msuser.infra.mqueue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class MsNotificationPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final Queue notificationQueue;

    public void sendNotification(String email, String event) throws JsonProcessingException {
        var json = convertIntoJson(email, event);
        rabbitTemplate.convertAndSend(notificationQueue.getName(), json);
    }

    private String convertIntoJson(String UserEmail, String eventType) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("email", UserEmail);
        map.put("event", eventType);
        map.put("date", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE));

        return mapper.writeValueAsString(map);
    }
}
