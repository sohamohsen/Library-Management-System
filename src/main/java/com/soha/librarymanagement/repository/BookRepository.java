package com.soha.librarymanagement.repository;

import com.soha.librarymanagement.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    Page<Book> findByTitleContainingIgnoreCaseOrIsbnContainingIgnoreCase(String term, String term1, Pageable pageable);

    boolean existsByIsbnIgnoreCaseAndIdNot(String newIsbn, Integer id);

    boolean existsByIsbnIgnoreCase(String isbn);
}
