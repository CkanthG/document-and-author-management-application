package com.krieger.document.exception;

/**
 * To handle DocumentReferenceException.
 */
public class DocumentReferenceException extends RuntimeException{
    // error message passed via constructor
    public DocumentReferenceException(String message){
        super(message);
    }
}
