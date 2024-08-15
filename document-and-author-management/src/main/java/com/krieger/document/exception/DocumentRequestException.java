package com.krieger.document.exception;

/**
 * To handle DocumentRequestException.
 */
public class DocumentRequestException extends RuntimeException{
    // error message passed via constructor
    public DocumentRequestException(String message) {
        super(message);
    }
}
