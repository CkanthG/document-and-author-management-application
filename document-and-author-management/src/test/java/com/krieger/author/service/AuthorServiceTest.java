package com.krieger.author.service;

import com.krieger.author.entity.Author;
import com.krieger.author.exception.AuthorNotFoundException;
import com.krieger.author.mapper.AuthorMapper;
import com.krieger.author.models.AllAuthorsResponse;
import com.krieger.author.models.AuthorRequest;
import com.krieger.author.models.AuthorResponse;
import com.krieger.author.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorServiceTest {

    @InjectMocks
    private AuthorService authorService;
    @Mock
    private AuthorRepository repository;
    @Mock
    private AuthorMapper mapper;

    private AuthorResponse authorResponse;
    private AuthorRequest authorRequest;
    private Author author;
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
    }

    @Test
    void test_save_author_should_successfully_save_the_author() {
        // when
        when(mapper.toAuthorEntity(authorRequest)).thenReturn(author);
        when(repository.save(author)).thenReturn(author);
        when(mapper.toAuthorResponseModel(author)).thenReturn(authorResponse);

        // then
        AuthorResponse response = authorService.saveAuthor(authorRequest);
        assertEquals(authorRequest.firstName(), response.getFirstName());
        assertEquals(authorRequest.lastName(), response.getLastName());
        assertEquals(authorResponse.getId(), response.getId());

        // verify
        verify(mapper, times(1))
                .toAuthorEntity(authorRequest);
        verify(repository, times(1))
                .save(author);
        verify(mapper, times(1))
                .toAuthorResponseModel(author);
    }

    @Test
    void test_update_author_should_successfully_update_the_author() {
        // when
        when(repository.findById(authorId)).thenReturn(Optional.of(author));
        when(mapper.toAuthorEntity(authorRequest)).thenReturn(author);
        when(repository.save(author)).thenReturn(author);
        when(mapper.toAuthorResponseModel(author)).thenReturn(authorResponse);

        // then
        AuthorResponse response = authorService.updateAuthor(authorRequest, authorId);
        assertEquals(authorRequest.firstName(), response.getFirstName());
        assertEquals(authorRequest.lastName(), response.getLastName());
        assertEquals(authorResponse.getId(), response.getId());

        // verify
        verify(repository, times(1))
                .findById(1L);
        verify(mapper, times(1))
                .toAuthorEntity(authorRequest);
        verify(repository, times(1))
                .save(author);
        verify(mapper, times(1))
                .toAuthorResponseModel(author);
    }

    @Test
    void test_update_author_should_throw_author_not_found_exception_when_invalid_author_id_passed() {
        assertThrows(
                AuthorNotFoundException.class,
                () -> authorService.updateAuthor(null, authorId)
        );
    }

    @Test
    void test_get_all_authors_should_return_matched_authors_when_search_by_first_name_and_last_name(){
        // given
        Page<Author> authorResponsePage = new PageImpl<>(List.of(author), pageable, 1);
        // when
        when(repository.findAllByFirstNameAndLastName(firstName, lastName, pageable)).thenReturn(authorResponsePage);
        when(mapper.toAuthorResponseModel(author)).thenReturn(authorResponse);

        // then
        AllAuthorsResponse actual = authorService.getAllAuthors(firstName, lastName, pageNumber, pageSize, sort);
        assertEquals((int) authorResponsePage.getTotalElements(), actual.totalElements());

        // verify
        verify(repository, times(1))
                .findAllByFirstNameAndLastName(firstName, lastName, pageable);
        verify(mapper, times(1))
                .toAuthorResponseModel(author);
    }

    @Test
    void test_get_all_authors_should_successfully_return_matched_authors_when_search_by_at_least_one_filter_first_name_or_last_name(){
        // given
        Page<Author> authorResponsePage = new PageImpl<>(List.of(author), pageable, 1);
        // when
        when(repository.findAllByFirstNameOrLastName(firstName, null, pageable)).thenReturn(authorResponsePage);
        when(mapper.toAuthorResponseModel(author)).thenReturn(authorResponse);

        // then
        AllAuthorsResponse actual = authorService.getAllAuthors(firstName, null, pageNumber, pageSize, sort);
        assertEquals((int) authorResponsePage.getTotalElements(), actual.totalElements());

        // verify
        verify(repository, times(1))
                .findAllByFirstNameOrLastName(firstName, null, pageable);
        verify(mapper, times(1))
                .toAuthorResponseModel(author);
    }

    @Test
    void test_get_all_authors_should_successfully_return_all_authors_when_no_filters_passed() {
        // given
        Author author1 = Author.builder().firstName("sree").lastName("kanth").build();
        Page<Author> authorResponsePage = new PageImpl<>(List.of(author, author1), pageable, 2);
        // when
        when(repository.findAll(pageable)).thenReturn(authorResponsePage);
        when(mapper.toAuthorResponseModel(author)).thenReturn(authorResponse);

        // then
        AllAuthorsResponse actual = authorService.getAllAuthors(null, null, pageNumber, pageSize, sort);
        assertEquals((int) authorResponsePage.getTotalElements(), actual.totalElements());

        // verify
        verify(repository, times(1))
                .findAll(pageable);
        verify(mapper, times(1))
                .toAuthorResponseModel(author);
    }

    @Test
    void test_find_author_by_id_should_successfully_return_author_when_valid_author_id_passed() {
        // when
        when(repository.findById(authorId)).thenReturn(Optional.of(author));
        when(mapper.toAuthorResponseModel(author)).thenReturn(authorResponse);

        // then
        AuthorResponse actual = authorService.findAuthorById(authorId);
        assertEquals(authorRequest.firstName(), actual.getFirstName());
        assertEquals(authorRequest.lastName(), actual.getLastName());

        // verify
        verify(repository, times(1))
                .findById(authorId);
        verify(mapper, times(1))
                .toAuthorResponseModel(author);
    }

    @Test
    void test_find_author_by_id_should_throw_author_not_found_exception_when_invalid_author_id_passed() {
        assertThrows(
                AuthorNotFoundException.class,
                () -> authorService.findAuthorById(authorId)
        );
    }

    @Test
    void test_delete_author_by_id_should_successfully_delete_the_author() {
        // when
        when(repository.findById(authorId)).thenReturn(Optional.of(author));

        // then
        authorService.deleteAuthorById(authorId);

        //verify
        verify(repository, times(1))
                .findById(authorId);
    }

    @Test
    void test_delete_author_by_id_should_throw_author_not_found_exception_when_invalid_author_id_passed() {
        assertThrows(
                AuthorNotFoundException.class,
                () -> authorService.deleteAuthorById(authorId)
        );
    }
}
