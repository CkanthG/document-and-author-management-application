package com.krieger.document.repository;

import com.krieger.document.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * To provide abstraction on Documents CRUD operations.
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    // accept title and body both parameters with pagination object to return response.
    Page<Document> findAllByTitleAndBody(String title, String body, Pageable pageable);
    // accept title or body any one param and pagination object to return response.
    Page<Document> findAllByTitleOrBody(String title, String body, Pageable pageable);
}
