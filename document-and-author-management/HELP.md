# Document and Author Management Web Application

## This Code Challenge is built with Spring Boot 3.3.2 (LTS), Java 21 (LTS) and Maven.

## Base Requirements
`Develop a feature-rich web application for managing documents and authors.
Each document has a title, body, authors, and references.
Authors have a first name and a last name.`

### Implemented RESTful services for Authors and Documents:
#### - Adding, deleting, editing, and viewing authors.
#### - Adding, deleting, editing, and viewing documents.
#### - Store all data in a PostgreSQL database, used flyway migration for creating initial tables and populated initial data.
#### - Written unit tests to ensure the correctness of the key functionalities.
#### - Written integration tests to ensure the correctness of the implemented code end to end.

### Additional Requirements:
#### - Implemented necessary CRUD operations.
#### - Maintained proper documentation and coding standards.
#### - Included error handling mechanisms to provide meaningful feedback to users in case of failures.
#### - Implemented validation for input data to ensure data integrity and security.
#### - Designed and documented all API endpoints using clear and consistent naming conventions and proper HTTP methods.

### Bonus Requirements:
#### - Documented the APIs using a framework Swagger for improved API documentation for client integration.
#### - Implemented Authentication and Authorization to secure the endpoints, allowing only authorized user roles(DOCUMENT Role and AUTHOR Role) to access and modify data.
#### - Implemented a message queue system using kafka message broker to publish author data(as initial step sending author data, later will send all kind of events(documents and author) to kafka based on requirement).
#### - Implemented a kafka consumer within the web app to consume events from the kafka message broker(kafka topic).
#### - When an event is received from kafka topic, extracted the information and deleted the specified author and all documents related to it.
#### - Dockerized for PostgreSQL and kafka message-broker.

## Instructions to Install and Start the Application

#### Install Docker Desktop in your local machine in-order to install all required software, follow below link for how to install docker desktop.
`https://docs.docker.com/manuals/`
### There are 2 approaches to run the application
### Approach 1:
#### Navigate to **document-and-author-management** folder in terminal.
#### Run below command, to install PostgreSQL, ZooKeeper and Kafka Docker images into your local docker.
`docker compose up -d`
##### Note1: make sure all software are installed properly or not?, by checking docker desktop UI or check in terminal by run below command.
`docker ps`
##### Note2: If some ports need to modify, then make sure change the configuration details in application.yml file.
#### Run below command, it will download all necessary dependencies and run application for you.
`mvn spring-boot:run`

#### Approach 1 Note: In this approach you can connect to postgres database, you can verify the database was created or not, before application startup, same for other docker images you can connect and check default configurations.

### Approach 2:
#### Now navigate to project : **document-and-author-management** location in terminal.
#### Run below command, it will download all docker images and necessary application dependencies and run application for you.
`mvn spring-boot:run`

#### Approach 2 Note: In this approach you can connect to postgres database or other docker images once your application starts successfully, once application stopped all docker images will be stopped automatically.

## Instruction to Use Application
#### Navigate to browser and open below url.
`http://localhost:9090/swagger-ui/index.html`
#### To access all document related endpoints, click button(Authorize) on right side of the swagger and enter username : "krieger-document" and password : "krieger-document".
#### To access all author related endpoints, click button(Authorize) on right side of the swagger and enter username : "krieger-author" and password : "krieger-author".

#### Note: To change the username and password, please refer to application.yml file and update it accordingly.
### Implemented AOP logging for enhanced request debugging. Default log level is set to INFO in application.yml. Enable DEBUG level for detailed logging.
