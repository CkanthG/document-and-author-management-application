package com.krieger.advice;

import com.krieger.author.exception.AuthorNotFoundException;
import com.krieger.author.exception.AuthorRequestException;
import com.krieger.document.exception.DocumentNotFoundException;
import com.krieger.document.exception.DocumentReferenceException;
import com.krieger.document.exception.DocumentRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

/**
 * To handle all kind of application exceptions and send proper messages.
 */
@RestControllerAdvice
public class ApplicationGenericExceptionAdvice {

    /**
     * To handle all kind of in valid method argument exceptions and send proper message.
     *
     * @param argumentNotValidException thrown when there is no valid argument passed.
     * @return response entity with custom generic field error response class.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GenericFieldErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException argumentNotValidException
    ) {
        // to hold the field error
        var errors = new HashMap<String, String>();
        argumentNotValidException.getBindingResult()
                .getFieldErrors()
                .forEach(
                        error -> errors.put(error.getField(), error.getDefaultMessage()) // adding field errors to hashmap.
                );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        new GenericFieldErrorResponse(errors)
                );
    }

    /**
     * To handle AuthorNotFoundException & DocumentNotFoundException exceptions and send proper error message.
     *
     * @param exception thrown where there is no author or no document found with requested ID's.
     * @return response entity with custom generic error response class.
     */
    @ExceptionHandler({AuthorNotFoundException.class, DocumentNotFoundException.class})
    public ResponseEntity<GenericErrorMessage> handleNotFoundExceptions(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        // catch thrown messages from AuthorNotFoundException & DocumentNotFoundException and prepare below error object.
                        new GenericErrorMessage(HttpStatus.NOT_FOUND.name(), exception.getLocalizedMessage())
                );
    }

    /**
     * To handle DocumentReferenceException exception and send proper error message.
     *
     * @param documentException thrown when there is no proper reference found with specified ID.
     * @return response entity with custom generic error response class.
     */
    @ExceptionHandler(DocumentReferenceException.class)
    public ResponseEntity<GenericErrorMessage> handleDocumentReferenceException(DocumentReferenceException documentException) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        // catch thrown message from DocumentReferenceException and prepare below error object.
                        new GenericErrorMessage(HttpStatus.BAD_REQUEST.name(), documentException.getLocalizedMessage())
                );
    }

    /**
     * To handle AuthorRequestException, DocumentRequestException and other exceptions to send appropriate error messages.
     *
     * @param exception thrown from application.
     * @return response entity with custom generic error response class.
     */
    @ExceptionHandler({Exception.class, AuthorRequestException.class, DocumentRequestException.class})
    public ResponseEntity<GenericErrorMessage> handleAllExceptions(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        // catch thrown message from Exception and prepare below error object.
                        new GenericErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.name(), exception.getLocalizedMessage())
                );
    }
}
