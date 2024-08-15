package com.krieger.author.controller;

import com.krieger.author.models.AllAuthorsResponse;
import com.krieger.author.models.AuthorRequest;
import com.krieger.author.models.AuthorResponse;
import com.krieger.author.service.AuthorService;
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
 * To accept all author related web requests and delegate to service layer.
 */
@RestController
@RequestMapping("/api/v1/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService service;

    /**
     * Creates a new author.
     *
     * @param request Author creation request.
     * @return Created author with HTTP CREATED status.
     */
    @PostMapping
    public ResponseEntity<AuthorResponse> saveAuthor(@RequestBody @Valid AuthorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.saveAuthor(request));
    }

    /**
     * Updates an existing author.
     *
     * @param request Updated author data.
     * @param authorId ID of the author to update.
     * @return Updated author with HTTP OK status.
     */
    @PutMapping("/{author-id}")
    public ResponseEntity<AuthorResponse> updateAuthor(
            @RequestBody @Valid AuthorRequest request,
            @PathVariable("author-id") Long authorId
    ) {
        return ResponseEntity.ok(service.updateAuthor(request, authorId));
    }

    /**
     * Retrieves a paginated list of authors.
     *
     * @param firstName Optional filter by the author's first name. If null, this filter is ignored.
     * @param lastName Optional filter by the author's last name. If null, this filter is ignored.
     * @param page The page number to retrieve, starting from 0.
     * @param size The number of records per page.
     * @param sort Sorting criteria (comma-separated list of field,direction pairs).
     * @return An AllAuthorsResponse object containing the list of authors metadata.
     */
    @GetMapping
    public ResponseEntity<AllAuthorsResponse> getAllAuthors(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort
    ) {
        return ResponseEntity.ok(service.getAllAuthors(firstName, lastName, page, size, sort));
    }

    /**
     * Retrieves an author by ID.
     *
     * @param authorId ID of the author to retrieve.
     * @return Retrieved author with HTTP OK status.
     */
    @GetMapping("/{author-id}")
    public ResponseEntity<AuthorResponse> getAuthorById(@PathVariable("author-id") Long authorId) {
        return ResponseEntity.ok(service.findAuthorById(authorId));
    }

    /**
     * Deletes an author by ID.
     *
     * @param authorId ID of the author to delete.
     * @return HTTP NO_CONTENT status.
     */
    @DeleteMapping("/{author-id}")
    public ResponseEntity<Void> deleteAuthorById(@PathVariable("author-id") Long authorId) {
        service.deleteAuthorById(authorId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
