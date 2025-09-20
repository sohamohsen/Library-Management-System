package com.soha.librarymanagement.service;

import com.soha.librarymanagement.dto.BookCopyRequest;
import com.soha.librarymanagement.dto.BookCopyResponse;
import com.soha.librarymanagement.entity.Book;
import com.soha.librarymanagement.entity.BookCopy;
import com.soha.librarymanagement.entity.BookCopyStatus;
import com.soha.librarymanagement.entity.SystemUser;
import com.soha.librarymanagement.repository.BookCopyRepository;
import com.soha.librarymanagement.repository.BookCopyStatusRepository;
import com.soha.librarymanagement.repository.BookRepository;
import com.soha.librarymanagement.repository.SystemUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookCopyService {

    private final BookCopyRepository copyRepository;
    private final BookRepository bookRepository;
    private final BookCopyStatusRepository statusRepository;
    private final SystemUserRepository userRepo;

    /* Create */
    @Transactional
    public BookCopyResponse create(BookCopyRequest req, String username) {
        Integer creatorId = userRepo.findByUsername(username)
                .map(SystemUser::getId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));

        String barcode = req.barcode().trim();
        if (copyRepository.existsByBarcodeIgnoreCase(barcode)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Barcode already exists");
        }

        Book book = bookRepository.findById(req.bookId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        BookCopyStatus status = statusRepository.findById(req.statusId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status not found"));

        BookCopy copy = BookCopy.builder()
                .book(book)
                .barcode(barcode)
                .status(status)
                .acquiredAt(req.acquiredAt() != null ? req.acquiredAt() : LocalDateTime.now())
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .createBy(creatorId)
                .build();

        try {
            BookCopy saved = copyRepository.save(copy);
            return toResponse(saved);
        } catch (DataIntegrityViolationException e) {
            log.error("Create BookCopy failed: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invalid or duplicate data");
        }
    }

    /* Update */
    @Transactional
    public BookCopyResponse update(Integer id, BookCopyRequest req) {
        BookCopy copy = copyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book copy not found"));

        boolean changed = false;

        // 1) Barcode (optional)
        if (req.barcode() != null) {
            String newBarcode = req.barcode().trim();
            if (newBarcode.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Barcode cannot be empty");
            }
            if (!newBarcode.equalsIgnoreCase(copy.getBarcode())
                    && copyRepository.existsByBarcodeIgnoreCaseAndIdNot(newBarcode, id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Barcode already exists for another copy");
            }
            copy.setBarcode(newBarcode);
            changed = true;
        }

        // 2) Book (optional)
        if (req.bookId() != null) {
            Book book = bookRepository.findById(req.bookId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
            copy.setBook(book);
            changed = true;
        }

        // 3) Status (optional)
        if (req.statusId() != null) {
            BookCopyStatus status = statusRepository.findById(req.statusId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status not found"));
            copy.setStatus(status);
            changed = true;
        }

        // 4) AcquiredAt (optional)
        if (req.acquiredAt() != null) {
            // Optionally validate not in the future
            if (req.acquiredAt().isAfter(LocalDateTime.now())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Acquired date cannot be in the future");
            }
            copy.setAcquiredAt(req.acquiredAt());
            changed = true;
        }

        if (!changed) {
            // Nothing to update
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No updatable fields provided");
        }

        copy.setUpdateAt(LocalDateTime.now());

        try {
            BookCopy saved = copyRepository.save(copy);
            return toResponse(saved);
        } catch (DataIntegrityViolationException e) {
            log.error("Update BookCopy failed: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invalid or duplicate data");
        }
    }


    /* Delete */
    @Transactional
    public void delete(Integer id) {
        if (!copyRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book copy not found");
        }
        try {
            copyRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            log.error("Delete BookCopy failed: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Book copy is linked and cannot be deleted");
        }
    }

    /* Get one */
    @Transactional(Transactional.TxType.SUPPORTS)
    public BookCopyResponse getById(Integer id) {
        BookCopy copy = copyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book copy not found"));
        return toResponse(copy);
    }

    /* List (paged) */
    @Transactional(Transactional.TxType.SUPPORTS)
    public Page<BookCopyResponse> list(Pageable pageable) {
        return copyRepository.findAll(pageable).map(this::toResponse);
    }

    /* Search with null-safe specification chaining (no deprecated where()) */
    @Transactional(Transactional.TxType.SUPPORTS)
    public Page<BookCopyResponse> search(String barcode, Integer statusId, Integer bookId, Pageable pageable) {
        Specification<BookCopy> spec = (root, query, cb) -> cb.conjunction();

        if (barcode != null && !barcode.isBlank()) {
            spec = spec.and(BookCopySpecifications.hasBarcode(barcode));
        }
        if (statusId != null) {
            spec = spec.and(BookCopySpecifications.hasStatus(statusId));
        }
        if (bookId != null) {
            spec = spec.and(BookCopySpecifications.hasBook(bookId));
        }

        return copyRepository.findAll(spec, pageable).map(this::toResponse);
    }

    /* Mapper */
    private BookCopyResponse toResponse(BookCopy c) {
        return new BookCopyResponse(
                c.getId(),
                c.getBook().getId(),
                c.getBook().getTitle(),
                c.getBarcode(),
                c.getStatus().getId(),
                c.getStatus().getStatus(),          // map the status CODE (not getStatus())
                // If your DTO includes description, add c.getStatus().getDescription() here.
                c.getAcquiredAt(),
                c.getCreateAt(),
                c.getUpdateAt(),
                c.getCreateBy()
        );
    }
}
