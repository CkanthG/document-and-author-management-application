package com.krieger.document.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.krieger.author.models.AuthorResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.ToString;

import java.util.Set;

/**
 * Represents document response with document metadata.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class DocumentResponse {
    Long id;
    String title;
    String body;
    Set<AuthorResponse> authors;
    Set<DocumentResponse> references;
}
