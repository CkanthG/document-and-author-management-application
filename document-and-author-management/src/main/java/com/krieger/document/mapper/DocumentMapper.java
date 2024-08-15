package com.krieger.document.mapper;

import com.krieger.author.entity.Author;
import com.krieger.author.exception.AuthorNotFoundException;
import com.krieger.author.models.AuthorResponse;
import com.krieger.document.entity.Document;
import com.krieger.document.exception.DocumentNotFoundException;
import com.krieger.document.exception.DocumentReferenceException;
import com.krieger.document.models.DocumentRequest;
import com.krieger.document.models.DocumentResponse;
import java.util.Objects;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * To map Document entity to DocumentResponse object and vise versa.
 */
@Component
public class DocumentMapper {
    /**
     * Converts a DocumentRequest object into a Document entity.
     *
     * @param request The DocumentRequest object containing the data for the document.
     * @param documentId The ID of the current document, used to prevent self-referencing.
     * @return A Document entity populated with data from the request.
     * @throws AuthorNotFoundException If an author ID is 0, indicating an invalid author.
     * @throws DocumentNotFoundException If a reference ID is 0, indicating an invalid document reference.
     * @throws DocumentReferenceException If the document references itself.
     */
    public Document toDocumentEntity(DocumentRequest request, Long documentId) {
        return Document.builder()
                .title(request.title())
                .body(request.body())
                .authors(
                        request.authorIds().stream()
                                .map(this::mapToAuthorEntity)
                                .collect(Collectors.toSet())  // Collect results into a Set.
                )
                .references(
                        request.references() != null ?  // If references are provided, convert them.
                                request.references().stream()
                                        .map(refId -> mapToReferenceEntity(refId, documentId))
                                        .collect(Collectors.toSet())  // Collect results into a Set.
                                : null  // If no references, set to null.
                )
                .build();
    }

    /**
     * Converts an author ID to an Author entity.
     *
     * @param authorId The ID of the author.
     * @return An Author entity with the given ID.
     * @throws AuthorNotFoundException If the author ID is 0.
     */
    private Author mapToAuthorEntity(Long authorId) {
        if (authorId == 0) {
            throw new AuthorNotFoundException("No author found with ID : 0.");
        }
        return Author.builder().id(authorId).build();  // Build and return an Author entity.
    }

    /**
     * Converts a reference ID to a Document entity while validating it.
     *
     * @param referenceId The ID of the reference document.
     * @param documentId The ID of the current document to prevent self-referencing.
     * @return A Document entity with the given reference ID.
     * @throws DocumentNotFoundException If the reference ID is 0.
     * @throws DocumentReferenceException If the document references itself.
     */
    private Document mapToReferenceEntity(Long referenceId, Long documentId) {
        if (referenceId == 0) {
            throw new DocumentNotFoundException("No document found with ID : 0.");
        }
        if (documentId != null && Objects.equals(referenceId, documentId)) {
            throw new DocumentReferenceException("Same document should not be a reference document.");
        }
        return Document.builder().id(referenceId).build();  // Build and return a Document entity.
    }

    /**
     * Converts a Document entity into a DocumentResponse model.
     *
     * @param document The Document entity to convert.
     * @return A DocumentResponse model populated with the document's data.
     */
    public DocumentResponse toDocumentResponseModel(Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .title(document.getTitle())
                .body(document.getBody())
                .authors(
                        document.getAuthors().stream()
                                .map(this::mapToAuthorResponse)
                                .collect(Collectors.toSet())
                )
                .references(
                        document.getReferences() != null ?  // Check if there are any references.
                                document.getReferences().stream()
                                        .map(this::mapReferenceToDocumentResponse)
                                        .collect(Collectors.toSet())
                                : null  // If no references, set to null.
                )
                .build();
    }

    /**
     * Converts an Author entity into an AuthorResponse model.
     *
     * @param author The Author entity to convert.
     * @return An AuthorResponse model populated with the author's data.
     */
    private AuthorResponse mapToAuthorResponse(Author author) {
        return AuthorResponse.builder()
                .id(author.getId())
                .firstName(author.getFirstName())
                .lastName(author.getLastName())
                .build(); // Build and return the AuthorResponse model.
    }

    /**
     * Converts a referenced Document entity into a DocumentResponse model.
     *
     * @param reference The referenced Document entity to convert.
     * @return A simplified DocumentResponse model with only ID, title, and body.
     */
    private DocumentResponse mapReferenceToDocumentResponse(Document reference) {
        return DocumentResponse.builder()
                .id(reference.getId())
                .title(reference.getTitle())
                .body(reference.getBody())
                .build(); // Build and return the mapped reference as DocumentResponse model.
    }
}
