package com.krieger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class TestContainerBaseClass {

	@Container
	public static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
			.withDatabaseName("test")
			.withUsername("postgres")
			.withPassword("password")
			.withStartupTimeout(Duration.ofSeconds(60));

	@Container
	public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

	@DynamicPropertySource
	static void registerPgProperties(DynamicPropertyRegistry registry) {
		// PostgreSQL properties
		registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
		registry.add("spring.datasource.username", postgresContainer::getUsername);
		registry.add("spring.datasource.password", postgresContainer::getPassword);
		// Kafka properties
		registry.add("spring.kafka.producer.bootstrap-servers", kafka::getBootstrapServers);
	}

	@BeforeAll
	static void startContainer() {
		postgresContainer.start();
		kafka.start();
	}

	@AfterAll
	static void stopContainer() {
		postgresContainer.stop();
		kafka.stop();
	}

}
