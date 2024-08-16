package com.krieger.kafka;

import com.krieger.author.models.AuthorResponse;
import com.krieger.author.service.AuthorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final AuthorService authorService;

    @KafkaListener(topics = "${kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeAuthorResponseFromKafka(AuthorResponse authorResponse) {
        log.info(format("Consuming the AuthorResponse from kafka Topic, Author : %s, authorId : %s",
                        authorResponse,
                        authorResponse.getId())
        );
        authorService.deleteAuthorById(authorResponse.getId());
        log.info("Successfully author and its documents got deleted from DB.");
    }
}
