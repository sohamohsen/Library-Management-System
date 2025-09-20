package com.soha.librarymanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateCategoryDto {
    @NotBlank @Size(max = 120)
    private String name;

    @Size(max = 400)
    private String description;

    public CreateCategoryDto(Integer id, String name, String description) {
    }
}
