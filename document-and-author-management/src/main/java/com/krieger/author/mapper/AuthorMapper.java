package com.krieger.author.mapper;

import com.krieger.author.entity.Author;
import com.krieger.author.exception.AuthorRequestException;
import com.krieger.author.models.AuthorRequest;
import com.krieger.author.models.AuthorResponse;
import com.krieger.document.entity.Document;
import com.krieger.document.models.DocumentResponse;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * To map entity object to model object and vise versa.
 */
@Component
public class AuthorMapper {

    /**
     * To map model object to entity object.
     *
     * @param request model object is used to convert from model to entity.
     * @return newly created Author entity object.
     */
    public Author toAuthorEntity(AuthorRequest request) {
        if (request == null) {
            throw new AuthorRequestException("AuthorRequest object should not be null.");
        }
        return Author.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .build();
    }

    /**
     * To map entity object to model object.
     *
     * @param author entity object is used to convert from entity to model.
     * @return AuthorResponse model object.
     */
    public AuthorResponse toAuthorResponseModel(Author author) {
        return AuthorResponse.builder()
                .id(author.getId())
                .firstName(author.getFirstName())
                .lastName(author.getLastName())
                .documents(
                        // null check before loading author documents.
                        author.getDocuments() != null ? author.getDocuments()
                                .stream()
                                .map(this::mapToDocumentResponse)
                                .collect(Collectors.toSet()) : null
                )
                .build();
    }

    /**
     * To map document entity to document response object.
     * @param document entity object to map.
     * @return mapped document response object.
     */
    private DocumentResponse mapToDocumentResponse(Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .title(document.getTitle())
                .body(document.getBody())
                .build();
    }
}
