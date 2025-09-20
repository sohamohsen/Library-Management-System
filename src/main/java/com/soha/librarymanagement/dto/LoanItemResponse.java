package com.soha.librarymanagement.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanItemResponse {
    private Integer id;
    private Integer copyId;
    private String barcode;

    private Integer bookId;
    private String bookTitle;
    private String bookIsbn;     // عدّلي الاسم لو عندك isbn13/… مختلف

    private LocalDateTime returnedAt;
    private Double fineAmount;
}
