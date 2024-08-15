package com.krieger.document.mapper;

import com.krieger.author.entity.Author;
import com.krieger.author.exception.AuthorNotFoundException;
import com.krieger.document.entity.Document;
import com.krieger.document.exception.DocumentNotFoundException;
import com.krieger.document.exception.DocumentReferenceException;
import com.krieger.document.exception.DocumentRequestException;
import com.krieger.document.models.DocumentRequest;
import com.krieger.document.models.DocumentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentMapperTest {

    private DocumentMapper mapper;
    private Long documentId = 1L;
    private String documentTitle = "Document";
    private String documentBody = "Document Body";
    private Long authorId = 1L;
    private String firstName = "Sreekanth";
    private String lastName = "Gaddoju";

    @BeforeEach
    void setUp() {
        mapper = new DocumentMapper();
    }

    @Test
    void test_should_map_DocumentRequest_to_DocumentEntity() {
        // given
        DocumentRequest expected = new DocumentRequest(documentTitle, documentBody, Set.of(documentId), null);
        // then
        Document actual = mapper.toDocumentEntity(expected, 2L);
        assertEquals(expected.title(), actual.getTitle());
        assertEquals(expected.body(), actual.getBody());
        assertEquals(expected.authorIds().size(), actual.getAuthors().size());
    }

    @Test
    void test_should_map_DocumentRequest_to_DocumentEntity_when_documentId_null() {
        // given
        DocumentRequest expected = new DocumentRequest(documentTitle, documentBody, Set.of(documentId), null);
        // then
        Document actual = mapper.toDocumentEntity(expected, null);
        assertEquals(expected.title(), actual.getTitle());
        assertEquals(expected.body(), actual.getBody());
        assertEquals(expected.authorIds().size(), actual.getAuthors().size());
    }

    @Test
    void test_should_throw_document_request_exception_when_DocumentRequest_is_null() {
        assertThrows(
                DocumentRequestException.class,
                () -> mapper.toDocumentEntity(null, documentId)
        );
    }

    @Test
    void test_should_throw_document_reference_exception_when_documentId_and_reference_id_same() {
        assertThrows(
                DocumentReferenceException.class,
                () -> mapper.mapToReferenceEntity(documentId, documentId)
        );
    }

    @Test
    void test_should_throw_document_not_found_exception_when_documentId_is_0() {
        assertThrows(
                DocumentNotFoundException.class,
                () -> mapper.mapToReferenceEntity(0L, documentId)
        );
    }

    @Test
    void test_should_throw_author_not_found_exception_when_authorId_is_0() {
        assertThrows(
                AuthorNotFoundException.class,
                () -> mapper.mapToAuthorEntity(0L)
        );
    }

    @Test
    void test_should_map_DocumentEntity_to_DocumentResponse() {
        // given
        Document expected = new Document(
                documentId,
                documentTitle,
                documentBody,
                Set.of(Author.builder().id(authorId).firstName(firstName).lastName(lastName).build()),
                null
        );
        // then
        DocumentResponse actual = mapper.toDocumentResponseModel(expected);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getBody(), actual.getBody());
        assertEquals(expected.getAuthors().size(), actual.getAuthors().size());
    }
}
