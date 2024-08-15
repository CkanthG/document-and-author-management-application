package com.krieger.document.service;

import com.krieger.author.entity.Author;
import com.krieger.author.models.AuthorRequest;
import com.krieger.author.models.AuthorResponse;
import com.krieger.document.entity.Document;
import com.krieger.document.exception.DocumentNotFoundException;
import com.krieger.document.mapper.DocumentMapper;
import com.krieger.document.models.AllDocumentsResponse;
import com.krieger.document.models.DocumentRequest;
import com.krieger.document.models.DocumentResponse;
import com.krieger.document.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DocumentServiceTest {

    @InjectMocks
    private DocumentService documentService;
    @Mock
    private DocumentRepository repository;
    @Mock
    private DocumentMapper mapper;

    private DocumentRequest documentRequest;
    private DocumentResponse documentResponse;
    private Document document;
    private AuthorResponse authorResponse;
    private AuthorRequest authorRequest;
    private Author author;
    private Long documentId = 1L;
    private String documentTitle = "Document";
    private String documentBody = "Document Body";
    private Long authorId = 1L;
    private String firstName = "Sreekanth";
    private String lastName = "Gaddoju";
    private int pageNumber = 1;
    private int pageSize = 10;
    private int offSet = 0;
    private String[] sort = {"id" , "asc"};
    private Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "id"));

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // given
        authorRequest = new AuthorRequest(firstName, lastName);
        author = Author.builder()
                .id(authorId)
                .firstName(firstName)
                .lastName(lastName)
                .build();
        authorResponse = new AuthorResponse(
                authorId,
                firstName,
                lastName,
                null
        );
        documentRequest = new DocumentRequest(
                documentTitle,
                documentBody,
                Set.of(authorId),
                null
        );
        document = Document.builder()
                .id(documentId)
                .title(documentTitle)
                .body(documentBody)
                .authors(
                        Set.of(author)
                )
                .build();
        documentResponse = new DocumentResponse(
                documentId,
                documentTitle,
                documentBody,
                Set.of(authorResponse),
                null
        );
    }

    @Test
    void test_save_document_should_successfully_save_the_document() {
        // when
        when(mapper.toDocumentEntity(documentRequest, null)).thenReturn(document);
        when(repository.save(document)).thenReturn(document);
        when(mapper.toDocumentResponseModel(document)).thenReturn(documentResponse);

        // then
        DocumentResponse response = documentService.createDocument(documentRequest);
        assertEquals(documentRequest.title(), response.getTitle());
        assertEquals(documentRequest.body(), response.getBody());
        assertEquals(documentResponse.getId(), response.getId());

        // verify
        verify(mapper, times(1))
                .toDocumentEntity(documentRequest, null);
        verify(repository, times(1))
                .save(document);
        verify(mapper, times(1))
                .toDocumentResponseModel(document);
    }

    @Test
    void test_update_document_should_successfully_update_the_document() {
        // when
        when(repository.findById(documentId)).thenReturn(Optional.of(document));
        when(mapper.toDocumentEntity(documentRequest, documentId)).thenReturn(document);
        when(repository.save(document)).thenReturn(document);
        when(mapper.toDocumentResponseModel(document)).thenReturn(documentResponse);

        // then
        DocumentResponse response = documentService.updateDocument(documentRequest, documentId);
        assertEquals(documentRequest.title(), response.getTitle());
        assertEquals(documentRequest.body(), response.getBody());
        assertEquals(documentResponse.getId(), response.getId());

        // verify
        verify(repository, times(1))
                .findById(documentId);
        verify(mapper, times(1))
                .toDocumentEntity(documentRequest, documentId);
        verify(repository, times(1))
                .save(document);
        verify(mapper, times(1))
                .toDocumentResponseModel(document);
    }

    @Test
    void test_update_document_should_throw_document_not_found_exception_when_invalid_document_id_passed() {
        assertThrows(
                DocumentNotFoundException.class,
                () -> documentService.updateDocument(null, documentId)
        );
    }

    @Test
    void test_get_all_documents_should_return_matched_documents_when_search_by_title_and_body(){
        // given
        Page<Document> documentsResponsePage = new PageImpl<>(List.of(document), pageable, 1);
        // when
        when(repository.findAllByTitleAndBody(documentTitle, documentBody, pageable)).thenReturn(documentsResponsePage);
        when(mapper.toDocumentResponseModel(document)).thenReturn(documentResponse);

        // then
        AllDocumentsResponse actual = documentService.getAllDocuments(documentTitle, documentBody, pageNumber, pageSize, sort);
        assertEquals((int) documentsResponsePage.getTotalElements(), actual.totalElements());

        // verify
        verify(repository, times(1))
                .findAllByTitleAndBody(documentTitle, documentBody, pageable);
        verify(mapper, times(1))
                .toDocumentResponseModel(document);
    }

    @Test
    void test_get_all_documents_should_successfully_return_matched_documents_when_search_by_at_least_one_filter_title_or_body(){
        // given
        Page<Document> documentsResponsePage = new PageImpl<>(List.of(document), pageable, 1);
        // when
        when(repository.findAllByTitleOrBody(documentTitle, null, pageable)).thenReturn(documentsResponsePage);
        when(mapper.toDocumentResponseModel(document)).thenReturn(documentResponse);

        // then
        AllDocumentsResponse actual = documentService.getAllDocuments(documentTitle, null, pageNumber, pageSize, sort);
        assertEquals((int) documentsResponsePage.getTotalElements(), actual.totalElements());

        // verify
        verify(repository, times(1))
                .findAllByTitleOrBody(documentTitle, null, pageable);
        verify(mapper, times(1))
                .toDocumentResponseModel(document);
    }

    @Test
    void test_get_all_documents_should_successfully_return_all_documents_when_no_filters_passed() {
        // given
        Document document1 = Document.builder().title("Doc").body("Body").authors(Set.of(author)).build();
        Page<Document> documentsResponsePage = new PageImpl<>(List.of(document, document1), pageable, 2);
        // when
        when(repository.findAll(pageable)).thenReturn(documentsResponsePage);
        when(mapper.toDocumentResponseModel(document)).thenReturn(documentResponse);

        // then
        AllDocumentsResponse actual = documentService.getAllDocuments(null, null, pageNumber, pageSize, sort);
        assertEquals((int) documentsResponsePage.getTotalElements(), actual.totalElements());

        // verify
        verify(repository, times(1))
                .findAll(pageable);
        verify(mapper, times(1))
                .toDocumentResponseModel(document);
    }

    @Test
    void test_find_document_by_id_should_successfully_return_document_when_valid_document_id_passed() {
        // when
        when(repository.findById(documentId)).thenReturn(Optional.of(document));
        when(mapper.toDocumentResponseModel(document)).thenReturn(documentResponse);

        // then
        DocumentResponse actual = documentService.findDocumentById(documentId);
        assertEquals(documentRequest.title(), actual.getTitle());
        assertEquals(documentRequest.body(), actual.getBody());

        // verify
        verify(repository, times(1))
                .findById(authorId);
        verify(mapper, times(1))
                .toDocumentResponseModel(document);
    }

    @Test
    void test_find_document_by_id_should_throw_document_not_found_exception_when_invalid_document_id_passed() {
        assertThrows(
                DocumentNotFoundException.class,
                () -> documentService.findDocumentById(documentId)
        );
    }

    @Test
    void test_delete_document_by_id_should_successfully_delete_the_document() {
        // when
        when(repository.findById(documentId)).thenReturn(Optional.of(document));

        // then
        documentService.deleteDocumentById(documentId);

        //verify
        verify(repository, times(1))
                .findById(authorId);
    }

    @Test
    void test_delete_document_by_id_should_throw_document_not_found_exception_when_invalid_document_id_passed() {
        assertThrows(
                DocumentNotFoundException.class,
                () -> documentService.deleteDocumentById(documentId)
        );
    }

}
