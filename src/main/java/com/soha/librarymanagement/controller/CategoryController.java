package com.soha.librarymanagement.controller;

import com.soha.librarymanagement.dto.CategoryDto;
import com.soha.librarymanagement.dto.CreateCategoryDto;
import com.soha.librarymanagement.errorhandling.ApiResponse;
import com.soha.librarymanagement.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<?>> createCategory(
            @Valid @RequestBody CreateCategoryDto body) {

        CategoryDto created = categoryService.createCategory(body);
        return ResponseEntity
                .status(201)
                .body(ApiResponse.success(created, "Category created successfully", 201));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<?>> updateCategory(
            @PathVariable Integer id,
            @Valid @RequestBody CreateCategoryDto body) {

        CategoryDto updated = categoryService.updateCategory(id, body);
        return ResponseEntity
                .ok(ApiResponse.success(updated, "Category updated successfully", 200));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity
                .ok(ApiResponse.success(null, "Category deleted successfully", 200));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories, "Categories fetched successfully", 200));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDto>> getCategoryById(@PathVariable Integer id) {
        CategoryDto dto = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(dto, "Category fetched successfully", 200));
    }
}
