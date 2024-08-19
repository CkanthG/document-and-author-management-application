package com.krieger.document.service;

import com.krieger.document.entity.Document;
import com.krieger.document.models.AllDocumentsResponse;
import com.krieger.document.models.DocumentRequest;
import com.krieger.document.models.DocumentResponse;

import java.util.List;
import java.util.Set;

/**
 * Delegate all requests to implementation to get appropriate responses.
 */
public interface DocumentService {

    DocumentResponse saveDocument(DocumentRequest request);

    DocumentResponse updateDocument(DocumentRequest request, Long documentId);

    AllDocumentsResponse getAllDocuments(String title, String body, int page, int size, String[] sort);

    DocumentResponse getDocumentById(Long documentId);

    void deleteDocumentById(Long documentId);

    void deleteDocumentsByIds(Set<Long> documentIdsToDelete);

    List<Document> getDocumentReferences(Long documentId);

    void emptyReferencesByIds(Set<Long> documentIdsToUpdate);
}
