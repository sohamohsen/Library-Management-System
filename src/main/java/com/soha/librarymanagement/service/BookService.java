// BookService.java
package com.soha.librarymanagement.service;

import com.soha.librarymanagement.dto.BookDto;
import com.soha.librarymanagement.dto.CreateBookDto;
import com.soha.librarymanagement.entity.*;
import com.soha.librarymanagement.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final CategoryRepository categoryRepository;
    private final SystemUserRepository userRepo;

    @Transactional
    public BookDto create(CreateBookDto req, String username) {
        Integer creatorId = userRepo.findByUsername(username)
                .map(SystemUser::getId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));

        final String isbn = req.isbn().trim();

        if (bookRepository.existsByIsbnIgnoreCase(isbn)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ISBN already exists");
        }

        Author author = authorRepository.findById(req.authorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));
        Publisher publisher = publisherRepository.findById(req.publisherId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publisher not found"));
        Category category = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        Book book = Book.builder()
                .isbn(isbn)
                .title(req.title().trim())
                .language(req.language() == null ? null : req.language().trim())
                .author(author)
                .publisher(publisher)
                .category(category)
                .publicationYear(req.publicationYear())
                .edition(req.edition() == null ? null : req.edition().trim())
                .summary(req.summary())
                .createAt(java.time.LocalDateTime.now())
                .updateAt(java.time.LocalDateTime.now())
                .createBy(creatorId) // ← set here
                .build();

        try {
            Book saved = bookRepository.save(book);
            return toDto(saved);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate or invalid data");
        }
    }

    @Transactional
    public BookDto update(Integer id, CreateBookDto req) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        final String newIsbn = req.isbn().trim();
        if (bookRepository.existsByIsbnIgnoreCaseAndIdNot(newIsbn, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ISBN already exists for another book");
        }

        Author author = authorRepository.findById(req.authorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));
        Publisher publisher = publisherRepository.findById(req.publisherId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publisher not found"));
        Category category = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        book.setIsbn(newIsbn);
        book.setTitle(req.title().trim());
        book.setLanguage(req.language() == null ? null : req.language().trim());
        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setCategory(category);
        book.setPublicationYear(req.publicationYear());
        book.setEdition(req.edition() == null ? null : req.edition().trim());
        book.setSummary(req.summary());
        book.setUpdateAt(LocalDateTime.now());
        // keep original createAt & createBy

        try {
            Book saved = bookRepository.save(book);
            return toDto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate or invalid data");
        }
    }

    @Transactional
    public void delete(Integer id) {
        if (!bookRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        }
        try {
            bookRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // If there are dependent rows (e.g., copies/loans)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Book is in use and cannot be deleted");
        }
    }

    @Transactional
    public BookDto getById(Integer id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        return toDto(book);
    }

    @Transactional
    public Page<BookDto> search(String q, Pageable pageable) {
        Page<Book> page;
        if (q == null || q.isBlank()) {
            page = bookRepository.findAll(pageable);
        } else {
            String term = q.trim();
            page = bookRepository.findByTitleContainingIgnoreCaseOrIsbnContainingIgnoreCase(term, term, pageable);
        }
        return page.map(this::toDto);
    }

    private BookDto toDto(Book b) {
        return new BookDto(
                b.getId(),
                b.getIsbn(),
                b.getTitle(),
                b.getLanguage(),
                b.getAuthor() != null ? b.getAuthor().getId() : null,
                b.getAuthor() != null ? b.getAuthor().getFullName() : null,
                b.getPublisher() != null ? b.getPublisher().getId() : null,
                b.getPublisher() != null ? b.getPublisher().getName() : null,
                b.getCategory() != null ? b.getCategory().getId() : null,
                b.getCategory() != null ? b.getCategory().getName() : null,
                b.getPublicationYear(),
                b.getEdition(),
                b.getSummary(),
                b.getCreateAt(),
                b.getUpdateAt(),
                b.getCreateBy()
        );
    }
}
