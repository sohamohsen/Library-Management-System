package com.soha.librarymanagement.repository;

import com.soha.librarymanagement.entity.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface BookCopyRepository
        extends JpaRepository<BookCopy, Integer>,
        JpaSpecificationExecutor<BookCopy> {

    boolean existsByBarcodeIgnoreCase(String barcode);

    boolean existsByBarcodeIgnoreCaseAndIdNot(String barcode, Integer id);

    List<BookCopy> findAllByBarcodeIn(List<String> inputBarcodes);

    @Query("select c " +
            "from BookCopy c " +
            "join fetch c.status s " +
            "join fetch c.book b " +
            "where c.barcode in :barcodes")
    List<BookCopy> findAllDetailedByBarcodeIn(@Param("barcodes") Collection<String> barcodes);
}
