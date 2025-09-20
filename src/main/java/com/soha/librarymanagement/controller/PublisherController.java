package com.soha.librarymanagement.controller;

import com.soha.librarymanagement.dto.CreatePublisherDto;
import com.soha.librarymanagement.dto.PublisherDto;
import com.soha.librarymanagement.errorhandling.ApiResponse;
import com.soha.librarymanagement.service.PublisherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/publishers")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
public class PublisherController {

    private final PublisherService publisherService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CreatePublisherDto>> createPublisher(@Valid @RequestBody CreatePublisherDto dto) {
        return ResponseEntity.status(201)
                .body(ApiResponse.success(publisherService.createPublisher(dto), "Publisher created successfully", 201));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<CreatePublisherDto>> updatePublisher(
            @PathVariable Integer id,
            @Valid @RequestBody PublisherDto dto) {
        return ResponseEntity.ok(ApiResponse.success(publisherService.updatePublisher(id, dto), "Publisher updated successfully", 200));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable Integer id) {
        publisherService.deletePublisher(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<PublisherDto>>> getAllPublishers() {
        return ResponseEntity.ok(ApiResponse.success(publisherService.getAllPublishers(), "Publishers fetched successfully", 200));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PublisherDto>> getPublisherById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(publisherService.getPublisherById(id), "Publisher fetched successfully", 200));
    }
}
