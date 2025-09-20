package com.soha.librarymanagement.repository;

import com.soha.librarymanagement.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Integer> {
    boolean existsByNameIgnoreCaseAndIdNot(String newName, Integer id);

    boolean existsByNameIgnoreCase(String name);
}
