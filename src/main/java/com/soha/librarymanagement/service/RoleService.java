package com.soha.librarymanagement.service;

import com.soha.librarymanagement.dto.RoleDto;
import com.soha.librarymanagement.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(role -> new RoleDto(role.getId(), role.getRole()))
                .toList();
    }

}
