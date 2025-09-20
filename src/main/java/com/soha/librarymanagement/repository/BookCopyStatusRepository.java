package com.soha.librarymanagement.repository;

import com.soha.librarymanagement.entity.BookCopyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookCopyStatusRepository extends JpaRepository<BookCopyStatus, Integer> {

    Optional<BookCopyStatus> findByStatusIgnoreCase(String status);

}
