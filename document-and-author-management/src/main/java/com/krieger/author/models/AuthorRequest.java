package com.krieger.author.models;

import jakarta.validation.constraints.NotBlank;

/**
 * To accepting the author metadata to store in DB.
 *
 * @param firstName is author firstname accept from user.
 * @param lastName is author lastname accept from user.
 */
public record AuthorRequest(
        @NotBlank(message = "Author first name should not be empty or null.")
        String firstName,
        @NotBlank(message = "Author last name should not be empty or null.")
        String lastName
) {
}
