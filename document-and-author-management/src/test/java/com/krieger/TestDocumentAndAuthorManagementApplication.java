package com.krieger;

import org.springframework.boot.SpringApplication;

public class TestDocumentAndAuthorManagementApplication {

	public static void main(String[] args) {
		SpringApplication.from(DocumentAndAuthorManagementApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
