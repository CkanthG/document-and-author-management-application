package com.krieger.author.models;

/**
 * Represents the pageable metadata.
 *
 * @param sort            An object representing the sorting criteria used in the query.
 * @param pageNumber The number of elements in the current page.
 * @param pageSize            The size of the page (number of elements per page).
 * @param offset          The current page number (starting from 0).
 */
public record CustomPageable(
        CustomSort sort,
        Integer pageNumber,
        Integer pageSize,
        Integer offset
) {
}
