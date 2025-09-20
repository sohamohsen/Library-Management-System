package com.soha.librarymanagement.service;

import com.soha.librarymanagement.dto.CategoryDto;
import com.soha.librarymanagement.dto.CreateCategoryDto;
import com.soha.librarymanagement.entity.Category;
import com.soha.librarymanagement.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryDto createCategory(CreateCategoryDto dto) {
        log.info("Request received to create category: name='{}', description='{}'",
                dto.getName(), dto.getDescription());

        String name = dto.getName().trim();

        // 1. Check for existing category
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            log.warn("Category creation failed - category with name '{}' already exists", name);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category already exists");
        }

        // 2. Build new category
        Category category = Category.builder()
                .name(name)
                .description(dto.getDescription() == null ? null : dto.getDescription().trim())
                .build();

        try {
            // 3. Save to DB
            Category saved = categoryRepository.save(category);
            log.info("Category successfully created with ID: {} and name: '{}'",
                    saved.getId(), saved.getName());
            return toCategoryDto(saved);
        } catch (DataIntegrityViolationException e) {
            // 4. Race condition handling
            log.error("DataIntegrityViolationException while creating category '{}': {}",
                    name, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category already exists");
        } catch (Exception e) {
            log.error("Unexpected error occurred while creating category '{}': {}", name, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create category");
        }
    }

    @Transactional
    public CategoryDto updateCategory(Integer id, CreateCategoryDto dto) {
        String newName = dto.getName().trim();
        String newDesc = dto.getDescription() == null ? null : dto.getDescription().trim();

        log.info("Update category request: id={}, name='{}'", id, newName);

        // 1) Load existing or 404
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        // 2) Ensure name is unique among OTHER rows
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(newName, id)) {
            log.warn("Update failed - name '{}' is already used by another category", newName);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category name already exists");
        }

        // 3) Mutate and save
        category.setName(newName);
        category.setDescription(newDesc);

        try {
            Category saved = categoryRepository.save(category);
            log.info("Category updated: id={}, name='{}'", saved.getId(), saved.getName());
            return toCategoryDto(saved);
        } catch (DataIntegrityViolationException e) {
            // safety net for DB unique constraints
            log.error("Constraint error updating category id {} with name '{}': {}", id, newName, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category name already exists");
        } catch (Exception e) {
            log.error("Unexpected error updating category id {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update category");
        }
    }

    @Transactional
    public void deleteCategory(Integer id) {
        log.info("Delete category request received for ID: {}", id);
        if (!categoryRepository.existsById(id)) {
            log.warn("Delete failed - category with ID {} not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        }
        try {
            categoryRepository.deleteById(id);
            log.info("Category with ID {} successfully deleted", id);
        } catch (Exception e) {
            log.error("Error occurred while deleting category ID {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete category");
        }
    }

    public CategoryDto getCategoryById(Integer id) {
        log.info("Fetching category by ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Category with ID {} not found", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
                });
        log.info("Category found: id={}, name='{}'", category.getId(), category.getName());
        return CategoryDto.from(category);
    }

    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            log.warn("No categories found in the database");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No categories found");
        }
        log.info("Retrieved {} categories from the database", categories.size());
        return categories.stream()
                .map(this::toCategoryDto)
                .toList();
    }

    private CreateCategoryDto toDto(Category c) {
        log.debug("Mapping Category entity to DTO: id={}, name={}", c.getId(), c.getName());
        return new CreateCategoryDto(c.getId(), c.getName(), c.getDescription());
    }

    private CategoryDto toCategoryDto(Category c) {
        log.debug("Mapping Category entity to DTO: id={}, name={}", c.getId(), c.getName());
        return new CategoryDto(c.getId(), c.getName(), c.getDescription());
    }
}