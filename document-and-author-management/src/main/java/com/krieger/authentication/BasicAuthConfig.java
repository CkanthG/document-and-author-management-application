package com.krieger.authentication;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * To read specific("basic.auth") properties from properties(.yml, .yaml or .properties) file.
 */
@Configuration
@ConfigurationProperties(prefix = "basic.auth")
@Data
public class BasicAuthConfig {
    BasicAuthCredentials document;
    BasicAuthCredentials author;
}
