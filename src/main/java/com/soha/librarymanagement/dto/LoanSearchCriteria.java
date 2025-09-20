package com.soha.librarymanagement.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanSearchCriteria {
    private Integer memberId;
    private Integer status;
    private Boolean overdueOnly;
    private LocalDateTime createdFrom;
    private LocalDateTime createdTo;
    private LocalDateTime dueFrom;
    private LocalDateTime dueTo;
    private Integer createdById;
    private String q;
}
