package com.soha.librarymanagement.dto;

import java.time.LocalDateTime;

public record BookCopyResponse(
        Integer id,
        Integer bookId,
        String bookTitle,
        String barcode,
        Integer statusId,
        String statusName,
        LocalDateTime acquiredAt,
        LocalDateTime createAt,
        LocalDateTime updateAt,
        Integer createBy
) {}
