package com.soha.librarymanagement.repository;

import com.soha.librarymanagement.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {
    boolean existsByFullNameIgnoreCaseAndIdNot(String newName, Integer id);

    boolean existsByFullNameIgnoreCase(String fullName);
}
