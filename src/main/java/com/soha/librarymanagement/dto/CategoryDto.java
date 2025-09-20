package com.soha.librarymanagement.dto;

import com.soha.librarymanagement.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    private Integer id;
    private String name;
    private String description;

    public static CategoryDto from(Category c) {
        return new CategoryDto(c.getId(), c.getName(), c.getDescription());
    }
}