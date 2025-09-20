package com.soha.librarymanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record BookCopyRequest(
         Integer bookId,
         @Size(max = 64) String barcode,
         Integer statusId,
         LocalDateTime acquiredAt
) {}
