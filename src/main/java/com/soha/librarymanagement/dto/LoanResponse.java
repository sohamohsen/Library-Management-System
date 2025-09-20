package com.soha.librarymanagement.dto;

import com.soha.librarymanagement.dto.LoanItemResponse;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanResponse {
    private Integer id;
    private Integer memberId;
    private Integer status; // 0=OPEN,1=RETURNED,2=CANCELLED,3=OVERDUE
    private LocalDateTime dueAt;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Integer createdById;
    private List<LoanItemResponse> items;   // ← هنملّاها بتفاصيل الكتاب
}
