# Use a minimal base image for the final stage
FROM eclipse-temurin:21-jre-jammy

# Set the working directory
WORKDIR /app

# Copy the Spring Boot JAR to the working directory
COPY target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 9090

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
