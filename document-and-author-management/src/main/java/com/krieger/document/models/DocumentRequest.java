package com.krieger.document.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

/**
 * Represents document request parameters.
 *
 * @param title of the document.
 * @param body of the document.
 * @param authorIds list of authors who wrote this document.
 * @param references list of document references to support this document.
 */
public record DocumentRequest(
    @NotBlank(message = "Document title should not be null or empty.")
    String title,
    @NotBlank(message = "Document body should not be null or empty.")
    String body,
    @NotEmpty(message = "Document authors should not be null or empty.")
    Set<Long> authorIds,
    Set<Long> references
) {
}
