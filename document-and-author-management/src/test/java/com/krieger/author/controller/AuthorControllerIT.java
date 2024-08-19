package com.krieger.author.controller;

import com.krieger.author.models.AllAuthorsResponse;
import com.krieger.author.models.AuthorRequest;
import com.krieger.author.models.AuthorResponse;
import com.krieger.author.repository.AuthorRepository;
import com.krieger.kafka.KafkaProducer;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthorControllerIT {

    @Autowired
    private AuthorRepository repository;

    @LocalServerPort
    private int port;

    private String authorUrl;

    private TestRestTemplate testRestTemplate;

    @Autowired
    KafkaProducer kafkaProducer;

    AuthorRequest authorRequest;

    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
            .withUsername("krieger")
            .withPassword("krieger")
            .withDatabaseName("test");

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))
            .withEmbeddedZookeeper();


    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        // PostgreSQL properties
        registry.add("spring.datasource.url", () -> postgreSQLContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> postgreSQLContainer.getUsername());
        registry.add("spring.datasource.password", () -> postgreSQLContainer.getPassword());

        // Kafka properties
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @BeforeAll
    static void beforeAll() {
        postgreSQLContainer.start();
        kafka.start();
        System.setProperty("POSTGRES_PORT", postgreSQLContainer.getMappedPort(5432).toString());
        System.setProperty("KAFKA_SERVER", kafka.getBootstrapServers());
        System.setProperty("KAFKA_ADVERTISED_LISTENERS", kafka.getBootstrapServers());
    }

    @BeforeEach
    public void setUp() {
        testRestTemplate = new TestRestTemplate(
                new RestTemplateBuilder().basicAuthentication("krieger-author", "krieger-author")
        );
        authorUrl = "http://localhost:" + port + "/api/v1/authors";
        authorRequest = new AuthorRequest("Sreekanth", "G");
    }

    @AfterEach
    public void cleanUp() {
        repository.deleteAll();
    }

    @AfterAll
    static void afterAll() {
        postgreSQLContainer.stop();
        kafka.stop();
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
