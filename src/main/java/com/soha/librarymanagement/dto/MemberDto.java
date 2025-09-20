package com.soha.librarymanagement.dto;


import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MemberDto {
    @NotBlank @Size(max = 120)
    private String fullName;


    @NotBlank @Email @Size(max = 160)
    private String email;


    @NotBlank @Size(min = 6, max = 30)
    private String phone;


    @Size(max = 255)
    private String address;


    @Past(message = "dateOfBirth must be in the past")
    private LocalDate dateOfBirth;


    @NotNull
    private Boolean active;
}