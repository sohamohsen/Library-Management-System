// BookResponse.java
package com.soha.librarymanagement.dto;

import java.time.LocalDateTime;

public record BookDto(
        Integer id,
        String isbn,
        String title,
        String language,
        Integer authorId,
        String authorName,
        Integer publisherId,
        String publisherName,
        Integer categoryId,
        String categoryName,
        Integer publicationYear,
        String edition,
        String summary,
        LocalDateTime createAt,
        LocalDateTime updateAt,
        Integer createBy
) {}
