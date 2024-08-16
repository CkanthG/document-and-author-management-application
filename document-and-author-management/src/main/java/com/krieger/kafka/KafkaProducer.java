package com.krieger.kafka;

import com.krieger.author.models.AuthorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, AuthorResponse> kafkaTemplate;
    private final Environment environment;

    public void sendAuthorInformation(AuthorResponse author) {
        Message<AuthorResponse> message = MessageBuilder
                .withPayload(author)
                .setHeader(KafkaHeaders.TOPIC, environment.getProperty("kafka.topic"))
                .build();
        kafkaTemplate.send(message);
    }
}
