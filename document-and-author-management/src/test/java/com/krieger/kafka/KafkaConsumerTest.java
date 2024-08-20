package com.krieger.kafka;

import com.krieger.author.models.AuthorResponse;
import com.krieger.author.service.AuthorServiceImpl;
import com.krieger.document.service.DocumentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class KafkaConsumerTest {

    @InjectMocks
    KafkaConsumer kafkaConsumer;
    @Mock
    AuthorServiceImpl authorServiceImpl;
    @Mock
    DocumentServiceImpl documentServiceImpl;

    AuthorResponse authorResponse;
    Long authorId = 1L;
    String firstName = "test first name";
    String lastName = "test last name";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // given
        authorResponse = new AuthorResponse(
                authorId,
                firstName,
                lastName,
                null
        );
    }

    @Test
    void test_consume_author_response_from_kafka_should_receive_author_response_and_delete_if_author_do_not_have_any_documents() {
        // when
        doNothing().when(documentServiceImpl).updateDocumentReferencesAndDeleteAuthor(authorId);

        // then
        kafkaConsumer.consumeAuthorResponseFromKafka(authorResponse);

        // verify
        verify(documentServiceImpl, times(1))
                .updateDocumentReferencesAndDeleteAuthor(authorId);
    }

}
