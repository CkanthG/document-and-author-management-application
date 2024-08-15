package com.krieger.document.models;

import com.krieger.author.models.CustomPageable;
import com.krieger.author.models.CustomSort;

import java.util.List;
/**
 * Represents the paginated response for a list of authors, including pagination and sorting metadata.
 *
 * @param content         The list of DocumentResponse objects for the current page.
 * @param pageable        An object containing pagination information (page number, size, offset).
 * @param totalElements   The total number of author records across all pages.
 * @param totalPages      The total number of pages based on the page size and total elements.
 * @param sort            An object representing the sorting criteria used in the query.
 * @param numberOfElements The number of elements in the current page.
 * @param size            The size of the page (number of elements per page).
 * @param number          The current page number (starting from 0).
 */
public record AllDocumentsResponse(
        List<DocumentResponse> content,
        CustomPageable pageable,
        Integer totalElements,
        Integer totalPages,
        CustomSort sort,
        Integer numberOfElements,
        Integer size,
        Integer number
) {
}
