package com.krieger.authentication;

import lombok.Data;

/**
 * To read properties from properties(.yml, .yaml and .properties) file.
 */
@Data
public class BasicAuthCredentials {
    private String username;
    private String password;
    private String role;
}
