package com.projtechhub.techhub.kafka.producer;

import com.projtechhub.techhub.entities.User;
import com.projtechhub.techhub.kafka.config.KafkaProducerConfig;
import com.projtechhub.techhub.kafka.event.UserPasswordChanged;
import com.projtechhub.techhub.kafka.event.UserRegistredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class MessageProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC_USER_REGISTRED = "user.registred";
    private static final String TOPIC_USER_PASSWORD_CHANGED = "user.passwordchanged";

    public void publishUserRegistredEvent(UserRegistredEvent event) {
        send(TOPIC_USER_REGISTRED, event.getEventId(), event);
    }

    public void publishUserPasswordChangedEvent(UserPasswordChanged event) {
        send(TOPIC_USER_PASSWORD_CHANGED, event.getEventId(), event);
    }
    public void send(String topic, String key, Object payload) {
        try{
            kafkaTemplate.send(topic, key, payload);
            log.info("Message sent to topic " + topic + " with key " + key);
        }catch(Exception e){
            log.error(e.getMessage());
        }
    }
}
