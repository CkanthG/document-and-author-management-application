package com.krieger.advice;

/**
 * To frame the error response to send to UI.
 * @param error used for type of error.
 * @param message used for explanation of the error in detailed.
 */
public record GenericErrorMessage(
        String error,
        String message
) {
}
