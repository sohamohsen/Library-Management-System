package com.soha.librarymanagement.repository;

import com.soha.librarymanagement.entity.SystemUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemUserRepository extends JpaRepository<SystemUser, Integer> {
    Optional<SystemUser> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<SystemUser> findByUsername(String username);

    boolean existsByUsernameIgnoreCase(String candidate);

    boolean existsByEmailIgnoreCase(String email);
}
