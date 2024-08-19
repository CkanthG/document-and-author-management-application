package com.krieger.kafka;

import com.krieger.author.entity.Author;
import com.krieger.author.models.AuthorResponse;
import com.krieger.author.service.AuthorServiceImpl;
import com.krieger.document.entity.Document;
import com.krieger.document.models.DocumentResponse;
import com.krieger.document.service.DocumentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Set;

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
        when(authorServiceImpl.getAuthorById(authorId)).thenReturn(authorResponse);

        // then
        kafkaConsumer.consumeAuthorResponseFromKafka(authorResponse);

        // verify
        verify(authorServiceImpl, times(1))
                .getAuthorById(authorId);
    }

    @Test
    void test_consume_author_response_from_kafka_should_receive_author_response_and_delete_author_and_all_documents_and_references_if_author_have_any_documents() {
        // given
        var documentId = 1L;
        var referenceDocumentId = 2L;
        var documentTitle = "document";
        var documentBody = "body";
        DocumentResponse referenceDocument = new DocumentResponse(
                referenceDocumentId,
                "ref document",
                documentBody,
                null,
                null
        );
        DocumentResponse documentResponse = new DocumentResponse(
                documentId,
                documentTitle,
                documentBody,
                null,
                Set.of(referenceDocument)
        );
        var author = Author.builder()
                .id(authorId)
                .firstName(firstName)
                .lastName(lastName)
                .build();
        var document = Document.builder()
                .id(referenceDocumentId)
                .title(documentTitle)
                .body(documentBody)
                .authors(
                        Set.of(author)
                )
                .build();

        authorResponse.setDocuments(Set.of(documentResponse));
        // when
        when(authorServiceImpl.getAuthorById(authorId)).thenReturn(authorResponse);
        // when
        when(documentServiceImpl.getDocumentReferences(documentId)).thenReturn(List.of(document));

        // then
        kafkaConsumer.consumeAuthorResponseFromKafka(authorResponse);

        // verify
        verify(authorServiceImpl, times(1))
                .getAuthorById(authorId);
        verify(documentServiceImpl, times(1))
                .getDocumentReferences(documentId);
    }
}
