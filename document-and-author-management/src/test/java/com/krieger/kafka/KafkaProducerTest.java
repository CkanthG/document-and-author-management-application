package com.krieger.kafka;

import com.krieger.author.models.AuthorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KafkaProducerTest {

    @InjectMocks
    private KafkaProducer kafkaProducer;

    @Mock
    KafkaTemplate<String, AuthorResponse> kafkaTemplate;

    @Mock
    Environment environment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_send_author_information_should_send_author_response_to_kafka() {
        // given
        AuthorResponse authorResponse = new AuthorResponse(
                1L,
                "test first name",
                "test last name",
                null
        );
        ArgumentCaptor<GenericMessage> captor = ArgumentCaptor.forClass(GenericMessage.class);

        // when
        when(environment.getProperty("kafka.topic")).thenReturn("document-and-author-test-topic");
        Message<AuthorResponse> message = MessageBuilder
                .withPayload(authorResponse)
                .setHeader(KafkaHeaders.TOPIC, environment.getProperty("kafka.topic"))
                .build();
        CompletableFuture<SendResult<String, AuthorResponse>> completableFuture = new CompletableFuture<>();
        when(kafkaTemplate.send(message)).thenReturn(completableFuture);

        // then
        kafkaProducer.sendAuthorInformation(authorResponse);

        // verify
        verify(kafkaTemplate, times(1)).send(captor.capture());

        assertEquals(authorResponse, captor.getValue().getPayload());
    }

}
