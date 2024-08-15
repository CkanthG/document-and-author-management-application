package com.krieger.author.exception;

/**
 * To handle AuthorRequestException.
 */
public class AuthorRequestException extends RuntimeException{
    // error message passed via constructor
    public AuthorRequestException(String message) {
        super(message);
    }
}
