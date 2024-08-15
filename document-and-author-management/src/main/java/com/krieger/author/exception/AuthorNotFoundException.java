package com.krieger.author.exception;

/**
 * This class is used to handle AuthorNotFoundException.
 */
public class AuthorNotFoundException extends RuntimeException{
    // exception message passing to constructor.
    public AuthorNotFoundException(String message) {
     super(message);
    }
}
