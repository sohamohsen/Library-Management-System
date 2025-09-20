package com.soha.librarymanagement.controller;

import com.soha.librarymanagement.dto.AuthorDto;
import com.soha.librarymanagement.dto.CreateAuthorDto;
import com.soha.librarymanagement.errorhandling.ApiResponse;
import com.soha.librarymanagement.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CreateAuthorDto>> createAuthor(@Valid @RequestBody CreateAuthorDto dto) {
        return ResponseEntity.status(201)
                .body(ApiResponse.success(authorService.createAuthor(dto), "Author created successfully", 201));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<CreateAuthorDto>> updateAuthor(
            @PathVariable Integer id,
            @Valid @RequestBody AuthorDto dto) {
        return ResponseEntity.ok(ApiResponse.success(authorService.updateAuthor(id, dto), "Author updated successfully", 200));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Integer id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<AuthorDto>>> getAllAuthors() {
        return ResponseEntity.ok(ApiResponse.success(authorService.getAllAuthors(), "Authors fetched successfully", 200));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuthorDto>> getAuthorById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(authorService.getAuthorById(id), "Author fetched successfully", 200));
    }
}