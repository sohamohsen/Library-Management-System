package com.soha.librarymanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePublisherDto(
        Integer id,

        @NotBlank(message = "Name is required")
        @Size(max = 120, message = "Name must be <= 120 characters")
        String name,

        Integer founderYear
) {}
