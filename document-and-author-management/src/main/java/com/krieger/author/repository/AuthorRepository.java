package com.krieger.author.repository;

import com.krieger.author.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * To provides data access operations for Author entities.
 */
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    // accept author firstName and lastName both parameters with pagination to return response.
    Page<Author> findAllByFirstNameAndLastName(String firstName, String lastName, Pageable pageable);
    // accept author firstName or lastName at least one parameter with pagination to return response.
    Page<Author> findAllByFirstNameOrLastName(String firstName, String lastName, Pageable pageable);
}
