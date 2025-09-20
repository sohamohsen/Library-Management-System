package com.soha.librarymanagement.dto;

import com.soha.librarymanagement.entity.SystemUser;

import java.time.LocalDateTime;

public record UserReadDto(
        Integer id,
        String username,
        String fullName,
        String email,
        String phone,
        String role,            // e.g. "LIBRARIAN"
        boolean enabled,
        LocalDateTime createAt,
        LocalDateTime updateAt,
        Integer createBy
) {
    public static UserReadDto toDto(SystemUser u) {
        return new UserReadDto(
                u.getId(),
                u.getUsername(),
                u.getFullName(),
                u.getEmail(),
                u.getPhone(),
                u.getRole() != null ? u.getRole().getRole() : null,
                u.isEnabled(),
                u.getCreateAt(),
                u.getUpdateAt(),
                u.getCreateBy()
        );
    }
}
