package com.soha.librarymanagement.repository;

import com.soha.librarymanagement.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    boolean existsByNameIgnoreCase(String trim);

    boolean existsByNameIgnoreCaseAndIdNot(String newName, Integer id);
}
