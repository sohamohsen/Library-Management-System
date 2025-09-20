package com.soha.librarymanagement.controller;

import com.soha.librarymanagement.dto.RegistrationRequest;
import com.soha.librarymanagement.entity.SystemUser;
import com.soha.librarymanagement.errorhandling.ApiResponse;
import com.soha.librarymanagement.repository.SystemUserRepository;
import com.soha.librarymanagement.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {

    private final AuthenticationService authService;
    private final SystemUserRepository userRepo;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> create(
            @Valid @RequestBody RegistrationRequest req,
            @AuthenticationPrincipal String username
    ) {
        Integer creatorId = userRepo.findByUsername(username)
                .map(SystemUser::getId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));

        req.setCreatedBy(creatorId);

        var saved = authService.createUser(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(saved, "User created successfully"));
    }

}