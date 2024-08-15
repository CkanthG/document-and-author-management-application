package com.krieger.author.models;

import com.krieger.document.models.DocumentResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

import java.util.Set;

/**
 * To build and send author response to UI.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorResponse {
        Long id;
        String firstName;
        String lastName;
        Set<DocumentResponse> documents;
}
