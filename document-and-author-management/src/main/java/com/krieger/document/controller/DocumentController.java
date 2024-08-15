package com.krieger.document.controller;

import com.krieger.document.models.AllDocumentsResponse;
import com.krieger.document.models.DocumentRequest;
import com.krieger.document.models.DocumentResponse;
import com.krieger.document.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * To accept all document related web requests and delegate to service layer.
 */
@RestController
@RequestMapping("api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService service;

    /**
     * Creates a new document.
     *
     * @param request Document creation request.
     * @return Created document with HTTP CREATED status.
     */
    @PostMapping
    public ResponseEntity<DocumentResponse> saveDocument(@RequestBody @Valid DocumentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createDocument(request));
    }

    /**
     * Updates an existing document.
     *
     * @param request Updated document data.
     * @param documentId ID of the document to update.
     * @return Updated document with HTTP OK status.
     */
    @PutMapping("/{document-id}")
    public ResponseEntity<DocumentResponse> updateDocument(
            @RequestBody @Valid DocumentRequest request,
            @PathVariable("document-id") Long documentId
    ) {
        return ResponseEntity.ok(service.updateDocument(request, documentId));
    }

    /**
     * Retrieves a paginated list of documents.
     *
     * @param page Zero-based page index.
     * @param size Page size.
     * @param sort Sorting criteria (comma-separated list of field,direction pairs).
     * @return an AllDocumentsResponse of documents with HTTP OK status.
     */
    @GetMapping
    public ResponseEntity<AllDocumentsResponse> getAllDocuments(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String body,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort
    ) {
        return ResponseEntity.ok(service.getAllDocuments(title, body, page, size, sort));
    }

    /**
     * Retrieves a document by its ID.
     *
     * @param documentId ID of the document to retrieve.
     * @return Retrieved document with HTTP OK status.
     */
    @GetMapping("/{document-id}")
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable("document-id") Long documentId) {
        return ResponseEntity.ok(service.findDocumentById(documentId));
    }

    /**
     * Deletes a document by its ID.
     *
     * @param documentId ID of the document to delete.
     * @return HTTP NO_CONTENT status.
     */
    @DeleteMapping("/{document-id}")
    public ResponseEntity<Void> deleteDocumentById(@PathVariable("document-id") Long documentId) {
        service.deleteDocumentById(documentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
