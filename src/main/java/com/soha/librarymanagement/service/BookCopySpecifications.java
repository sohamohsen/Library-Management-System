package com.soha.librarymanagement.service;

import com.soha.librarymanagement.entity.BookCopy;
import org.springframework.data.jpa.domain.Specification;

public final class BookCopySpecifications {
    private BookCopySpecifications() {}

    public static Specification<BookCopy> hasBarcode(String barcode) {
        if (barcode == null || barcode.isBlank()) return null;
        String like = "%" + barcode.trim().toLowerCase() + "%";
        return (root, cq, cb) -> cb.like(cb.lower(root.get("barcode")), like);
    }

    public static Specification<BookCopy> hasStatus(Integer statusId) {
        if (statusId == null) return null;
        return (root, cq, cb) -> cb.equal(root.get("status").get("id"), statusId);
    }

    public static Specification<BookCopy> hasBook(Integer bookId) {
        if (bookId == null) return null;
        return (root, cq, cb) -> cb.equal(root.get("book").get("id"), bookId);
    }
}
