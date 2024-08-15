package com.krieger.document.controller;

import com.krieger.TestContainerBaseClass;
import com.krieger.author.entity.Author;
import com.krieger.author.models.AuthorRequest;
import com.krieger.author.repository.AuthorRepository;
import com.krieger.document.models.AllDocumentsResponse;
import com.krieger.document.models.DocumentRequest;
import com.krieger.document.models.DocumentResponse;
import com.krieger.document.repository.DocumentRepository;
import org.junit.AfterClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DocumentControllerIT extends TestContainerBaseClass {

    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private DocumentRepository documentRepository;

    @LocalServerPort
    private int port;

    private String documentUrl;

    @Autowired
    private TestRestTemplate testRestTemplate;
    Author authorResponse;
    DocumentRequest documentRequest;

    @BeforeEach
    public void setUp() {
        documentUrl = "http://localhost:" + port + "/api/v1/documents";
        AuthorRequest authorRequest = new AuthorRequest("Sreekanth", "G");
        authorResponse = authorRepository.save(
                Author.builder()
                        .firstName(authorRequest.firstName())
                        .lastName(authorRequest.lastName())
                        .build()
        );
        documentRequest = new DocumentRequest("Document1", "Document Body1", Set.of(authorResponse.getId()), null);
    }

    @AfterEach
    public void cleanUp() {
        authorRepository.deleteAll();
        documentRepository.deleteAll();
    }

    @AfterClass
    public static void stopContainer() throws InterruptedException {
        postgresContainer.stop();
        Thread.sleep(5000);
    }

    @Test
    void test_save_document_should_return_success_status_code_and_response() {
        ResponseEntity<DocumentResponse> responseEntity = testRestTemplate.postForEntity(documentUrl, documentRequest, DocumentResponse.class);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(documentRequest.title(), Objects.requireNonNull(responseEntity.getBody()).getTitle());
        assertEquals(documentRequest.body(), responseEntity.getBody().getBody());
    }

    @Test
    void test_save_document_should_throw_bad_request_status_code_with_invalid_input_data() {
        documentRequest = new DocumentRequest(null, null, null, null);
        ResponseEntity<DocumentResponse> responseEntity = testRestTemplate.postForEntity(documentUrl, documentRequest, DocumentResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void test_update_document_should_return_success_status_code_and_response() {
        ResponseEntity<DocumentResponse> postForEntity = testRestTemplate.postForEntity(documentUrl, documentRequest, DocumentResponse.class);
        var document = postForEntity.getBody();
        documentRequest = new DocumentRequest("Updated Document1", "Updated Document Body1", Set.of(authorResponse.getId()), null);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<DocumentRequest> entity = new HttpEntity<>(documentRequest, httpHeaders);
        assert document != null;
        ResponseEntity<DocumentResponse> responseEntity = testRestTemplate.exchange(
                documentUrl + "/" + document.getId(),
                HttpMethod.PUT,
                entity,
                DocumentResponse.class
        );
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(documentRequest.title(), Objects.requireNonNull(responseEntity.getBody()).getTitle());
        assertEquals(documentRequest.body(), responseEntity.getBody().getBody());
    }

    @Test
    void test_update_document_should_throw_error_status_code_with_same_document_as_reference_document() {
        ResponseEntity<DocumentResponse> postForEntity = testRestTemplate.postForEntity(documentUrl, documentRequest, DocumentResponse.class);
        var document = postForEntity.getBody();
        assert document != null;
        documentRequest = new DocumentRequest("Updated Document1", "Updated Document Body1", Set.of(authorResponse.getId()), Set.of(document.getId()));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<DocumentRequest> entity = new HttpEntity<>(documentRequest, httpHeaders);
        ResponseEntity<DocumentResponse> responseEntity = testRestTemplate.exchange(
                documentUrl + "/" + document.getId(),
                HttpMethod.PUT,
                entity,
                DocumentResponse.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void test_update_document_should_throw_bad_request_status_code_with_invalid_update_data() {
        documentRequest = new DocumentRequest(null, null, null, null);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<DocumentRequest> entity = new HttpEntity<>(documentRequest, httpHeaders);
        ResponseEntity<DocumentResponse> responseEntity = testRestTemplate.exchange(
                documentUrl + "/1",
                HttpMethod.PUT,
                entity,
                DocumentResponse.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void test_get_all_documents_should_return_success_status_code() {
        testRestTemplate.postForEntity(documentUrl, documentRequest, DocumentResponse.class);
        ResponseEntity<AllDocumentsResponse> responseEntity = testRestTemplate.getForEntity(documentUrl, AllDocumentsResponse.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).content().size());
    }

    @Test
    void test_get_all_documents_should_return_empty_response_if_there_is_no_document_data_available() {
        ResponseEntity<AllDocumentsResponse> responseEntity = testRestTemplate.getForEntity(documentUrl, AllDocumentsResponse.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(0, Objects.requireNonNull(responseEntity.getBody()).content().size());
    }

    @Test
    void test_get_document_by_id_should_return_success_status_code_with_document_data() {
        ResponseEntity<DocumentResponse> entity = testRestTemplate.postForEntity(documentUrl, documentRequest, DocumentResponse.class);
        assert entity != null;
        var document = entity.getBody();
        assert document != null;
        ResponseEntity<DocumentResponse> responseEntity = testRestTemplate.getForEntity(
                documentUrl + "/" + document.getId(),
                DocumentResponse.class
        );
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(document.getId(), Objects.requireNonNull(responseEntity.getBody()).getId());
        assertEquals(document.getTitle(), Objects.requireNonNull(responseEntity.getBody()).getTitle());
        assertEquals(document.getBody(), Objects.requireNonNull(responseEntity.getBody()).getBody());
    }

    @Test
    void test_get_document_by_id_throw_error_status_code_with_invalid_document_id() {
        ResponseEntity<DocumentResponse> responseEntity = testRestTemplate.getForEntity(
                documentUrl + "/124",
                DocumentResponse.class
        );
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void test_delete_document_by_id_should_return_success_status_code_with_valid_document_id() {
        ResponseEntity<DocumentResponse> entity = testRestTemplate.postForEntity(documentUrl, documentRequest, DocumentResponse.class);
        assert entity != null;
        var document = entity.getBody();
        assert document != null;
        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
                documentUrl + "/" + document.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    void test_delete_document_by_id_throw_error_status_code_with_invalid_document_id() {
        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
                documentUrl + "/787",
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

}
