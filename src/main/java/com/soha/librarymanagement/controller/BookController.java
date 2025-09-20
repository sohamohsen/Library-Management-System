// BookController.java
package com.soha.librarymanagement.controller;

import com.soha.librarymanagement.dto.BookDto;

import com.soha.librarymanagement.dto.CreateBookDto;
import com.soha.librarymanagement.entity.SystemUser;
import com.soha.librarymanagement.errorhandling.ApiResponse;
import com.soha.librarymanagement.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
public class BookController {

    private final BookService bookService;


    @PostMapping("/create")
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody CreateBookDto body,
                                                 @AuthenticationPrincipal String username
    ) {

        BookDto created = bookService.create(body, username);
        return ResponseEntity.status(201).body(ApiResponse.success(created, "Book created successfully", 201));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable Integer id,
                                                            @Valid @RequestBody CreateBookDto body) {
        BookDto updated = bookService.update(id, body);
        return ResponseEntity.ok(ApiResponse.success(updated, "Book updated successfully", 200));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Paginated list with simple text search (title or ISBN)
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<?>>> list(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id,desc") String sort
    ) {
        String[] parts = sort.split(",", 2);
        Sort.Direction dir = parts.length > 1 && parts[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, parts[0]));

        Page<BookDto> result = bookService.search(q, pageable);
        return ResponseEntity.ok(ApiResponse.success(result, "Books fetched successfully", 200));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDto>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(bookService.getById(id), "Book fetched successfully", 200));
    }
}
