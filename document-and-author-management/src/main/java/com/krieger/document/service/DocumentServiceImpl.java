package com.krieger.document.service;

import com.krieger.author.models.CustomPageable;
import com.krieger.author.models.CustomSort;
import com.krieger.author.service.AuthorService;
import com.krieger.document.entity.Document;
import com.krieger.document.exception.DocumentNotFoundException;
import com.krieger.document.mapper.DocumentMapper;
import com.krieger.document.models.DocumentRequest;
import com.krieger.document.models.DocumentResponse;
import com.krieger.document.models.AllDocumentsResponse;
import com.krieger.document.repository.DocumentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * To delegate all incoming requests to repository layer or mapper, in order to process it.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository repository;
    private final DocumentMapper mapper;
    private final AuthorService authorService;

    /**
     * Creates a new document and returns its response representation.
     *
     * @param request Document creation request.
     * @return Created document response.
     */
    public DocumentResponse saveDocument(DocumentRequest request) {
        return mapper.toDocumentResponseModel(
                repository.save(mapper.toDocumentEntity(request, null))
        );
    }

    /**
     * Retrieves a paginated list of document responses.
     *
     * @param title Optional filter by the document's title. If null, this filter is ignored.
     * @param body Optional filter by the document's body. If null, this filter is ignored.
     * @param page      The page number to retrieve, starting from 0.
     * @param size      The number of records per page.
     * @param sort  Sorting criteria (comma-separated list of field,direction pairs).
     * @return An AllDocumentsResponse object containing the paginated list of documents and pagination metadata.
     */
    public AllDocumentsResponse getAllDocuments(String title, String body, int page, int size, String[] sort) {
        Sort.Direction direction =
                sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        // custom sort record framing
        var customSort = new CustomSort(sort[0], direction.name());
        Page<DocumentResponse> responsePage;
        if (title != null && body != null) {
            responsePage = repository.findAllByTitleAndBody(title, body, pageable)
                    .map(mapper::toDocumentResponseModel);
        } else if (title != null || body != null) {
            responsePage = repository.findAllByTitleOrBody(title, body, pageable)
                    .map(mapper::toDocumentResponseModel);
        } else {
            responsePage = repository.findAll(pageable)
                    .map(mapper::toDocumentResponseModel);
        }
        return getAllDocumentsResponse(responsePage, pageable, customSort);
    }

    /**
     * Constructs the AllDocumentsResponse object with the given list of documents and pagination details.
     *
     * @param responsePage The page of DocumentResponse objects for the current page.
     * @param pageable The Pageable object containing pagination and sorting details.
     * @param customSort The CustomSort object to include in the response metadata.
     * @return An AllDocumentsResponse object containing the list of documents and associated pagination metadata.
     */
    private AllDocumentsResponse getAllDocumentsResponse(
            Page<DocumentResponse> responsePage,
            Pageable pageable,
            CustomSort customSort
    ) {
        List<DocumentResponse> content = responsePage.getContent();
        var totalElements = (int) responsePage.getTotalElements();
        var totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize()); // calculating total pages.
        return new AllDocumentsResponse(
                content,
                new CustomPageable(
                        customSort,
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        (int) pageable.getOffset()
                ),
                totalElements,
                totalPages,
                customSort,
                content.size(),
                pageable.getPageSize(),
                pageable.getPageNumber()
        );
    }

    /**
     * Updates an existing document and returns its response representation.
     *
     * @param request Updated document data.
     * @param documentId ID of the document to update.
     * @return Updated document response.
     */
    public DocumentResponse updateDocument(DocumentRequest request, Long documentId) {
        // checking before updating the document by documentId
        findDocumentByDocumentId(documentId, "No document found with specified ID : %s to update.");
        var documentEntity = mapper.toDocumentEntity(request, documentId);
        documentEntity.setId(documentId);
        return mapper.toDocumentResponseModel(
                repository.save(documentEntity)
        );
    }

    /**
     * To find the document exist or not by using the documentId.
     *
     * @param documentId to identify document object.
     * @param format generic message format to send proper error message.
     * @throws DocumentNotFoundException when there is no document found with specified documentId.
     */
    private void findDocumentByDocumentId(Long documentId, String format) {
        repository.findById(documentId).orElseThrow(
                () -> new DocumentNotFoundException(
                        format(format, documentId)
                )
        );
    }

    /**
     * find document by its ID
     *
     * @param documentId ID of the document to fetch.
     * @return resulted document response.
     */
    public DocumentResponse getDocumentById(Long documentId) {
        return repository.findById(documentId)
                .map(mapper::toDocumentResponseModel)
                .orElseThrow(
                        () -> new DocumentNotFoundException(
                                format("No document found with specified ID : %s", documentId)
                        )
                );
    }

    /**
     * delete document by its ID
     *
     * @param documentId ID of the document to delete.
     */
    public void deleteDocumentById(Long documentId) {
        var document = getDocumentById(documentId);
        // Collect all document IDs to empty references
        Set<Long> documentIdsToUpdate = new HashSet<>();
        collectDocumentReferencesToEmpty(Set.of(document), documentIdsToUpdate);
        repository.deleteById(documentId);
    }

    /**
     * To get all document references from repository interface.
     *
     * @param documentId to identify the references.
     * @return list of referencing documents.
     */
    public List<Document> getDocumentReferences(Long documentId) {
        return repository.findDocumentsReferencing(documentId);
    }

    /**
     * To update document references.
     *
     * @param documentIdsToUpdate to identify the documents to update.
     */
    @Override
    public void emptyReferencesByIds(Set<Long> documentIdsToUpdate) {
        for (Long documentId : documentIdsToUpdate) {
            // Fetch the document by its ID
            Document document = repository.findById(documentId)
                    .orElseThrow(() -> new DocumentNotFoundException("Document not found with ID: " + documentId));

            // Remove only the references that are in the documentIdsToUpdate set
            Set<Document> updatedReferences = document.getReferences().stream()
                    .filter(refDoc -> !documentIdsToUpdate.contains(refDoc.getId()))
                    .collect(Collectors.toSet());

            // Update the document's references
            document.setReferences(updatedReferences);

            // Save the updated document back to the repository
            repository.save(document);
        }
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
        List<Document> references = getDocumentReferences(documentId);
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
    @Override
    @Transactional
    public void updateDocumentReferencesAndDeleteAuthor(Long authorId) {
        try {
            // Get the author response and written documents.
            var authorResponse = authorService.getAuthorById(authorId);

            // Collect all document IDs to empty references
            Set<Long> documentIdsToUpdate = new HashSet<>();
            var documents = authorResponse.getDocuments();
            collectDocumentReferencesToEmpty(documents, documentIdsToUpdate);

            // Finally, delete the author if required
            authorService.deleteAuthorById(authorId);

            log.info("Successfully removed documents and its references for the author with ID {}.", authorId);
        } catch (Exception ex) {
            log.error("Exception occurred while updating the author with ID {} and its document references. " +
                    "Rolling back. Error: {}", authorId, ex.getMessage());
            // Re-throw the exception to trigger rollback, in case of failures.
            throw ex;
        }
    }

    /**
     * To collect all documents to empty references.
     *
     * @param documents to identify the references.
     * @param documentIdsToUpdate to store all document reference IDs.
     */
    private void collectDocumentReferencesToEmpty(Set<DocumentResponse> documents, Set<Long> documentIdsToUpdate) {
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
            emptyReferencesByIds(documentIdsToUpdate);
        }
    }

    /**
     * To delete all documents at a time using repository interface.
     *
     * @param documentIds to delete all documents.
     */
    public void deleteDocumentsByIds(Set<Long> documentIds) {
        repository.deleteAllByIdInBatch(documentIds);
    }
}
