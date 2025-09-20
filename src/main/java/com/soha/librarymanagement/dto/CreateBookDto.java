// BookRequest.java
package com.soha.librarymanagement.dto;

import jakarta.validation.constraints.*;

public record CreateBookDto(
        @NotBlank @Size(max = 20) String isbn,
        @NotBlank @Size(max = 240) String title,
        @Size(max = 40) String language,
        @NotNull Integer authorId,
        @NotNull Integer publisherId,
        @NotNull Integer categoryId,
        @Positive @Max(3000) Integer publicationYear,
        @Size(max = 40) String edition,
        String summary
) {}
