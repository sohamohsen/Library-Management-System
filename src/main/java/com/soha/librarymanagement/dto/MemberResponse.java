package com.soha.librarymanagement.dto;


import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MemberResponse {
    private Integer id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private LocalDate dateOfBirth;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}