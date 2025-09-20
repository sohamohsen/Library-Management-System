package com.soha.librarymanagement.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanItemReturnRequest {
    @NotEmpty
    private List<@NotNull Integer> itemIds;
    // اختياري: لو عايزة تحددي returnedAt يدوي
    private LocalDateTime returnedAt;
}
