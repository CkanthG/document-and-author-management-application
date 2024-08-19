package com.krieger.kafka;

import com.krieger.author.models.AuthorResponse;
import com.krieger.author.service.AuthorServiceImpl;
import com.krieger.document.entity.Document;
import com.krieger.document.service.DocumentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final AuthorServiceImpl authorService;
    private final DocumentService documentService;

    @KafkaListener(topics = "${kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeAuthorResponseFromKafka(AuthorResponse authorResponse) {
        // delete author and all the documents associated with author and update references.
        updateAuthorDocumentsAndReferences(authorResponse.getId());
    }

    /**
     * To collect all documents IDs, including their references.
     *
     * @param documentId to identify the documents.
     * @param documentIdsToDelete to store all document IDs.
     */
    private void collectDocumentReferences(Long documentId, Set<Long> documentIdsToDelete) {
        // Avoid redundant lookups.
        if (!documentIdsToDelete.add(documentId)) {
            return; // If the documentId was already added skips here.
        }

        // Recursively collect document references.
        List<Document> references = documentService.getDocumentReferences(documentId);
        references.forEach(
                refDoc ->
                        // recursive call to identify reference documents.
                        collectDocumentReferences(
                                refDoc.getId(),
                                documentIdsToDelete
                        )
        );
    }

    /**
     * To delete author, its documents and update the references accordingly.
     *
     * @param authorId to identify documents and references.
     */
    @Transactional
    public void updateAuthorDocumentsAndReferences(Long authorId) {
        try {
            // Get the author response and written documents.
            var authorResponse = authorService.getAuthorById(authorId);

            // Collect all document IDs to empty references
            Set<Long> documentIdsToUpdate = new HashSet<>();
            var documents = authorResponse.getDocuments();
            if (documents != null) {
                documents.forEach(
                        document ->
                                // Collect all documents and their references.
                                collectDocumentReferences(
                                        document.getId(),
                                        documentIdsToUpdate
                                )
                );

                // Empty all references for the collected documents
                documentService.emptyReferencesByIds(documentIdsToUpdate);
            }

            // Finally, delete the author if required
            authorService.deleteAuthorById(authorId);

            log.info("Successfully removed documents and its references for the author with ID {}.", authorId);
        } catch (Exception ex) {
            log.error("Exception occurred while updating the author with ID {} and its document references. Rolling back. Error: {}", authorId, ex.getMessage());
            // Re-throw the exception to trigger rollback, in case of failures.
            throw ex;
        }
    }
}
