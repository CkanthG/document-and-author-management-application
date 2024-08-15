package com.krieger.author.mapper;

import com.krieger.author.entity.Author;
import com.krieger.author.exception.AuthorRequestException;
import com.krieger.author.models.AuthorRequest;
import com.krieger.author.models.AuthorResponse;
import com.krieger.document.entity.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AuthorMapperTest {

    private AuthorMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AuthorMapper();
    }

    @Test
    void test_should_map_AuthorRequest_To_AuthorEntity() {
        // given
        AuthorRequest request = new AuthorRequest("Sreekanth", "Gaddoju");
        // when
        Author author = mapper.toAuthorEntity(request);
        // then
        assertEquals(request.firstName(), author.getFirstName());
        assertEquals(request.lastName(), author.getLastName());
    }

    @Test
    void test_should_throw_author_request_exception_when_AuthorRequest_is_null() {
        assertThrows(
                AuthorRequestException.class,
                () -> mapper.toAuthorEntity(null)
        );
    }

    @Test
    void test_should_map_AuthorEntity_To_AuthorResponse() {
        // given
        Author author = Author.builder()
                .id(1L)
                .firstName("Sreekanth")
                .lastName("Gaddoju")
                .documents(
                        Set.of(
                                Document.builder().id(1L).build()
                        )
                )
                .build();
        // when
        AuthorResponse response = mapper.toAuthorResponseModel(author);
        // then
        assertEquals(author.getId(), response.getId());
        assertEquals(author.getFirstName(), response.getFirstName());
        assertEquals(author.getLastName(), response.getLastName());
        assertEquals(author.getDocuments().size(), response.getDocuments().size());
    }
}
