package com.soha.librarymanagement.controller;

import com.soha.librarymanagement.dto.RoleDto;
import com.soha.librarymanagement.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        List<RoleDto> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @PostMapping("/add")
    public ResponseEntity<RoleDto> addRole(@RequestBody RoleDto roleDto) {
        RoleDto updatedRole = roleService.addRole(roleDto);
        return ResponseEntity.ok(updatedRole);
    }

}
