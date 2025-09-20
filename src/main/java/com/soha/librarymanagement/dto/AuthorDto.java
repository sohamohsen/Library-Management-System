package com.soha.librarymanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthorDto(
        Integer id,

        @NotBlank(message = "Full name is required")
        @Size(max = 120, message = "Full name must be <= 120 characters")
        String fullName,

        @Size(max = 500, message = "Bio must be <= 500 characters")
        String bio
) {}