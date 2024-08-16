package com.krieger.author.controller;

import com.krieger.TestContainerBaseClass;
import com.krieger.author.models.AllAuthorsResponse;
import com.krieger.author.models.AuthorRequest;
import com.krieger.author.models.AuthorResponse;
import com.krieger.author.repository.AuthorRepository;
import com.krieger.kafka.KafkaProducer;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthorControllerIT extends TestContainerBaseClass {

    @Autowired
    private AuthorRepository repository;

    @LocalServerPort
    private int port;

    private String authorUrl;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    KafkaProducer kafkaProducer;

    AuthorRequest authorRequest;

    @BeforeEach
    public void setUp() {
        authorUrl = "http://localhost:" + port + "/api/v1/authors";
        authorRequest = new AuthorRequest("Sreekanth", "G");
    }

    @AfterEach
    public void cleanUp() {
        repository.deleteAll();
    }

    @AfterClass
    public static void stopContainer() throws InterruptedException {
        postgresContainer.stop();
        Thread.sleep(5000);
    }

    @Test
    void test_save_author_should_return_success_status_code_and_response() {
        ResponseEntity<AuthorResponse> responseEntity = testRestTemplate.postForEntity(authorUrl, authorRequest, AuthorResponse.class);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(authorRequest.firstName(), responseEntity.getBody().getFirstName());
        assertEquals(authorRequest.lastName(), responseEntity.getBody().getLastName());
    }

    @Test
    void test_save_author_should_throw_bad_request_status_code_with_invalid_input_data() {
        authorRequest = new AuthorRequest("", "");
        ResponseEntity<AuthorResponse> responseEntity = testRestTemplate.postForEntity(authorUrl, authorRequest, AuthorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void test_update_author_should_return_success_status_code_and_response() {
        ResponseEntity<AuthorResponse> postForEntity = testRestTemplate.postForEntity(authorUrl, authorRequest, AuthorResponse.class);
        var author = postForEntity.getBody();
        authorRequest = new AuthorRequest("Gaddoju", "Sreekanth");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<AuthorRequest> entity = new HttpEntity<>(authorRequest, httpHeaders);
        assert author != null;
        ResponseEntity<AuthorResponse> responseEntity = testRestTemplate.exchange(
                authorUrl + "/" + author.getId(),
                HttpMethod.PUT,
                entity,
                AuthorResponse.class
        );
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(authorRequest.firstName(), Objects.requireNonNull(responseEntity.getBody()).getFirstName());
        assertEquals(authorRequest.lastName(), responseEntity.getBody().getLastName());
    }

    @Test
    void test_update_author_should_throw_bad_request_status_code_with_invalid_update_data() {
        authorRequest = new AuthorRequest("", "");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<AuthorRequest> entity = new HttpEntity<>(authorRequest, httpHeaders);
        ResponseEntity<AuthorResponse> responseEntity = testRestTemplate.exchange(
                authorUrl + "/1",
                HttpMethod.PUT,
                entity,
                AuthorResponse.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void test_get_all_authors_should_return_success_status_code() {
        testRestTemplate.postForEntity(authorUrl, authorRequest, AuthorResponse.class);
        ResponseEntity<AllAuthorsResponse> responseEntity = testRestTemplate.getForEntity(authorUrl, AllAuthorsResponse.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).content().size());
    }

    @Test
    void test_get_all_authors_should_return_empty_response_if_there_is_no_author_data_available() {
        ResponseEntity<AllAuthorsResponse> responseEntity = testRestTemplate.getForEntity(authorUrl, AllAuthorsResponse.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(0, Objects.requireNonNull(responseEntity.getBody()).content().size());
    }

    @Test
    void test_get_author_by_id_should_return_success_status_code_with_author_data() {
        ResponseEntity<AuthorResponse> entity = testRestTemplate.postForEntity(authorUrl, authorRequest, AuthorResponse.class);
        assert entity != null;
        var author = entity.getBody();
        assert author != null;
        ResponseEntity<AuthorResponse> responseEntity = testRestTemplate.getForEntity(
                authorUrl + "/" + author.getId(),
                AuthorResponse.class
        );
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(author.getId(), Objects.requireNonNull(responseEntity.getBody()).getId());
        assertEquals(author.getFirstName(), Objects.requireNonNull(responseEntity.getBody()).getFirstName());
        assertEquals(author.getLastName(), Objects.requireNonNull(responseEntity.getBody()).getLastName());
    }

    @Test
    void test_get_author_by_id_throw_error_status_code_with_invalid_author_id() {
        ResponseEntity<AuthorResponse> responseEntity = testRestTemplate.getForEntity(
                authorUrl + "/45145",
                AuthorResponse.class
        );
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void test_delete_author_by_id_should_return_success_status_code_with_valid_author_id() {
        ResponseEntity<AuthorResponse> entity = testRestTemplate.postForEntity(authorUrl, authorRequest, AuthorResponse.class);
        assert entity != null;
        var author = entity.getBody();
        assert author != null;
        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
                authorUrl + "/" + author.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    void test_delete_author_by_id_throw_error_status_code_with_invalid_author_id() {
        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
                authorUrl + "/87845",
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void test_send_author_to_kafka_should_return_success_status_code_with_valid_author_id() {
        ResponseEntity<AuthorResponse> entity = testRestTemplate.postForEntity(authorUrl, authorRequest, AuthorResponse.class);
        assert entity != null;
        var author = entity.getBody();
        assert author != null;
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                authorUrl + "/" + author.getId() + "/send",
                HttpMethod.GET,
                null,
                String.class
        );
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Successfully Sent Author : " + author.getId() + " Information to Kafka", responseEntity.getBody());
    }

    @Test
    void test_send_author_to_kafka_should_throw_error_status_code_with_invalid_author_id() {
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                authorUrl + "/1/send",
                HttpMethod.GET,
                null,
                String.class
        );
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
}
