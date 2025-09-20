package com.soha.librarymanagement.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddLoanItemsRequest {
    @NotEmpty
    private List<@NotNull Integer> copyIds;
}