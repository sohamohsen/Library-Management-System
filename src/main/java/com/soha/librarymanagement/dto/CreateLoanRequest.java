package com.soha.librarymanagement.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLoanRequest {
    @NotNull
    private Integer memberId;

    private LocalDateTime dueAt;

    @NotEmpty
    private List<@NotBlank String> barcodes;  // بدل copyIds
}
