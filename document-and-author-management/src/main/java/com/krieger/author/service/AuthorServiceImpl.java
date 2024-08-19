package com.krieger.author.service;

import com.krieger.author.exception.AuthorNotFoundException;
import com.krieger.author.mapper.AuthorMapper;
import com.krieger.author.models.AuthorRequest;
import com.krieger.author.models.AuthorResponse;
import com.krieger.author.models.AllAuthorsResponse;
import com.krieger.author.models.CustomSort;
import com.krieger.author.models.CustomPageable;
import com.krieger.author.repository.AuthorRepository;
import com.krieger.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

/**
 * To delegate all incoming requests to repository layer or mapper, in order to process it.
 */
@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository repository;
    private final AuthorMapper mapper;
    private final KafkaProducer kafkaProducer;

    /**
     * To save author information.
     *
     * @param request is used for storing author information using mapper.
     * @return stored author response after mapping to model object.
     */
    public AuthorResponse saveAuthor(AuthorRequest request) {
        // convert saved author entity to model object.
        return mapper.toAuthorResponseModel(
                repository.save(
                        mapper.toAuthorEntity(request) // convert model to entity before saving to DB.
                )
        );
    }

    /**
     * To update author metadata.
     *
     * @param request is used for update the author information.
     * @param authorId is to identify the author to update it.
     * @return updated author response after mapping to model object.
     */
    public AuthorResponse updateAuthor(AuthorRequest request, Long authorId) {
        // find author before updating it.
        findAuthorByAuthorId(authorId, "No author found with specified ID : %s to update.");
        // convert model object to entity.
        var authorEntity = mapper.toAuthorEntity(request);
        authorEntity.setId(authorId);
        // convert saved author entity to author response object.
        return mapper.toAuthorResponseModel(repository.save(authorEntity));
    }

    /**
     * To find author data by authorId
     *
     * @param authorId to identify author.
     * @param format generic message format to send proper error message.
     * @throws AuthorNotFoundException when there is no author found with specified authorId.
     */
    private void findAuthorByAuthorId(Long authorId, String format) {
        repository.findById(authorId).orElseThrow(
                // if there is no author found, we need to send exception message to user/client.
                () -> new AuthorNotFoundException(
                        format(format, authorId)
                )
        );
    }

    /**
     * To get all authors information from DB.
     *
     * @param firstName Optional filter by the author's first name. If null, this filter is ignored.
     * @param lastName Optional filter by the author's last name. If null, this filter is ignored.
     * @param page      The page number to retrieve, starting from 0.
     * @param size      The number of records per page.
     * @param sort      An array containing the sort field and direction ("asc" or "desc").
     * @return An AllAuthorsResponse object containing the paginated list of authors and pagination metadata.
     */
    public AllAuthorsResponse getAllAuthors(String firstName, String lastName, int page, int size, String[] sort) {
        Sort.Direction direction =
                sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        // custom sort record framing
        var customSort = new CustomSort(sort[0], direction.name());
        Page<AuthorResponse> authorPage;

        if (firstName != null && lastName != null) {
            authorPage = repository.findAllByFirstNameAndLastName(firstName, lastName, pageable)
                    .map(mapper::toAuthorResponseModel);
        } else if (firstName != null || lastName != null) {
            authorPage = repository.findAllByFirstNameOrLastName(firstName, lastName, pageable)
                    .map(mapper::toAuthorResponseModel);
        } else {
            authorPage = repository.findAll(pageable)
                    .map(mapper::toAuthorResponseModel);
        }
        // Return the response, including the list of authors and pagination metadata.
        return getAllAuthorsResponse(authorPage.getContent(), pageable, customSort, (int) authorPage.getTotalElements());
    }

    /**
     * Constructs the AllAuthorsResponse object with the given list of authors and pagination details.
     *
     * @param result The list of AuthorResponse objects for the current page.
     * @param pageable The Pageable object containing pagination and sorting details.
     * @param customSort The CustomSort object to include in the response metadata.
     * @param totalElements The total number of authors across all pages.
     * @return An AllAuthorsResponse object containing the list of authors and associated pagination metadata.
     */
    private static AllAuthorsResponse getAllAuthorsResponse(
            List<AuthorResponse> result,
            Pageable pageable,
            CustomSort customSort,
            int totalElements
    ) {
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize()); // calculating total pages

        return new AllAuthorsResponse(
                result,
                new CustomPageable(
                        customSort,
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        (int) pageable.getOffset()
                ),
                totalElements,
                totalPages,
                customSort,
                result.size(),
                pageable.getPageSize(),
                pageable.getPageNumber()
        );
    }

    /**
     * To find author details by authorId to delegate request to getAuthorResponse method.
     *
     * @param authorId is used to identify Author resource.
     * @return fetched identified Author resource information.
     */
    public AuthorResponse getAuthorById(Long authorId) {
        return getAuthorResponseByAuthorId(authorId, "No author found with specified ID : %s fetch.");
    }

    /**
     * To find author details by authorId and map to author response object.
     *
     * @param authorId is used to identify Author resource in DB.
     * @param format generic message format to send proper error message.
     * @return fetched identified Author resource information from DB.
     * @throws AuthorNotFoundException when there is no author found with specified authorId.
     */
    private AuthorResponse getAuthorResponseByAuthorId(Long authorId, String format) {
        return repository.findById(authorId)
                // map optional author entity object to model object.
                .map(mapper::toAuthorResponseModel)
                .orElseThrow(
                        // if there is no author found, we need to send exception message to user/client.
                        () -> new AuthorNotFoundException(
                                format(format, authorId)
                        )
                );
    }

    /**
     * To delete the author resource by authorId.
     *
     * @param authorId is used to identify author resource in DB to delete.
     */
    public void deleteAuthorById(Long authorId) {
        findAuthorByAuthorId(authorId, "No author found with specified ID : %s to delete.");
        repository.deleteById(authorId);
    }

    /**
     * To send author information to kafka by ID.
     *
     * @param authorId - used to identify resource to send to kafka.
     * @return success response or exception message to UI.
     */
    public String sendAuthorToKafka(Long authorId) {
        var authorResponse = getAuthorResponseByAuthorId(
                authorId,
                "No author found with specified ID : %s to send to kafka."
        );
        kafkaProducer.sendAuthorInformation(authorResponse);
        return format("Successfully Sent Author : %s Information to Kafka", authorId);
    }
}
