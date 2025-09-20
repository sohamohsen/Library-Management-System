package com.soha.librarymanagement.dto;


import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateMemberDto {
    @NotBlank @Size(max = 120)
    private String fullName;

    @NotBlank @Email @Size(max = 160)
    private String email;

    @NotBlank @Size(min = 6, max = 30)
    private String phone;

    private LocalDate JoinedOn;
}