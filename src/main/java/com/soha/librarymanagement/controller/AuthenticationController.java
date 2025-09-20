package com.soha.librarymanagement.controller;

import com.soha.librarymanagement.dto.LoginRequest;
import com.soha.librarymanagement.errorhandling.ApiResponse;
import com.soha.librarymanagement.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginRequest req) {
        String token = authService.login(req.getEmail(), req.getPassword());
        return ResponseEntity.ok(ApiResponse.success(token, "Login successful"));
    }


}
