package com.krieger.document.exception;

/**
 * To handles DocumentNotFoundException.
 */
public class DocumentNotFoundException extends RuntimeException{
    // error message passed via constructor.
    public DocumentNotFoundException(String message) {
        super(message);
    }
}
