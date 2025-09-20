package com.soha.librarymanagement.controller;

import com.soha.librarymanagement.dto.BookCopyRequest;
import com.soha.librarymanagement.dto.BookCopyResponse;
import com.soha.librarymanagement.errorhandling.ApiResponse;
import com.soha.librarymanagement.service.BookCopyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/book-copies")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','STAFF')")
public class BookCopyController {

    private final BookCopyService copyService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<BookCopyResponse>> create(
            @Valid @RequestBody BookCopyRequest req,
            @AuthenticationPrincipal String username
    ) {
        BookCopyResponse created = copyService.create(req, username);
        return ResponseEntity.status(201).body(ApiResponse.success(created, "Book copy created successfully", 201));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<ApiResponse<BookCopyResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody BookCopyRequest req
    ) {
        BookCopyResponse updated = copyService.update(id, req);
        return ResponseEntity.ok(ApiResponse.success(updated, "Book copy updated successfully", 200));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        copyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookCopyResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(copyService.getById(id), "Book copy fetched successfully", 200));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<BookCopyResponse>>> list(
            @RequestParam(required = false) String barcode,
            @RequestParam(required = false) Integer statusId,
            @RequestParam(required = false) Integer bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id,desc") String sort
    ) {
        String[] parts = sort.split(",", 2);
        Sort.Direction dir = parts.length > 1 && parts[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, parts[0]));

        Page<BookCopyResponse> result = copyService.search(barcode, statusId, bookId, pageable);

        return ResponseEntity.ok(ApiResponse.success(result, "Book copies fetched successfully", 200));
    }
}
