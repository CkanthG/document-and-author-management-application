package com.krieger.author.service;

import com.krieger.author.models.AllAuthorsResponse;
import com.krieger.author.models.AuthorRequest;
import com.krieger.author.models.AuthorResponse;

/**
 * Delegate all requests to implementation layer to get appropriate response.
 */
public interface AuthorService {

    AuthorResponse saveAuthor(AuthorRequest request);

    AuthorResponse updateAuthor(AuthorRequest request, Long authorId);

    AllAuthorsResponse getAllAuthors(String firstName, String lastName, int page, int size, String[] sort);

    AuthorResponse getAuthorById(Long authorId);

    void deleteAuthorById(Long authorId);

    String sendAuthorToKafka(Long authorId);
}
