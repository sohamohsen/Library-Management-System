package com.soha.librarymanagement.service;

import com.soha.librarymanagement.dto.RegistrationRequest;
import com.soha.librarymanagement.dto.UserReadDto;
import com.soha.librarymanagement.entity.Role;
import com.soha.librarymanagement.entity.SystemUser;
import com.soha.librarymanagement.repository.RoleRepository;
import com.soha.librarymanagement.repository.SystemUserRepository;
import com.soha.librarymanagement.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final SystemUserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public UserReadDto createUser(RegistrationRequest req) {
        // Normalize inputs
        String email = req.getEmail().trim().toLowerCase();
        String preferredUsername = req.getUsername() != null ? req.getUsername().trim().toLowerCase() : null;

        // Fast checks
        if (userRepo.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        String username;
        if (preferredUsername != null && !preferredUsername.isBlank()) {
            if (userRepo.existsByUsernameIgnoreCase(preferredUsername)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already in use");
            }
            username = preferredUsername;
        } else {
            username = generateUniqueUsername(req.getFullName(), email);
        }

        Role role = roleRepo.findById(req.getRoleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid roleId"));

        SystemUser user = SystemUser.builder()
                .fullName(req.getFullName().trim())
                .email(email)
                .username(username)
                .password(encoder.encode(req.getPassword()))
                .phone(req.getPhone())
                .role(role)
                .enabled(req.isEnabled())
                .createBy(req.getCreatedBy())
                .build();

        try {
            SystemUser saved = userRepo.save(user);
            return UserReadDto.toDto(saved);
        } catch (DataIntegrityViolationException ex) {
            // Race-condition safe fallback (unique constraints at DB)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email or username already exists");
        }
    }

    /** Generates a unique username and resolves collisions. */
    private String generateUniqueUsername(String fullName, String email) {
        String base = (fullName != null && !fullName.isBlank()
                ? fullName.split("\\s+")[0]
                : email.substring(0, email.indexOf('@')))
                .toLowerCase()
                .replaceAll("[^a-z0-9._-]", "");

        if (base.isBlank()) base = "user";

        String candidate = base;
        int counter = 1;
        while (userRepo.existsByUsernameIgnoreCase(candidate)) {
            candidate = base + counter++;
        }
        return candidate;
    }

    /** Email-or-username login (email preferred) already handled by your UserDetailsServiceImpl. */
    public String login(String emailOrUsername, String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(emailOrUsername, password)
        );
        var user = (UserDetails) auth.getPrincipal();
        return jwtService.generateToken(user);
    }
}