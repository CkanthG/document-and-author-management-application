package com.krieger.author.models;

/**
 * Represents sorting criteria used in a query, including the property to sort by and the direction of the sort.
 *
 * @param property  The field or property by which the results are sorted (e.g., "firstName", "lastName").
 * @param direction The direction of sorting, either "asc" for ascending or "desc" for descending.
 */
public record CustomSort(
        String property,
        String direction
) {
}
