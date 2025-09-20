package com.soha.librarymanagement.controller;

import com.soha.librarymanagement.dto.CreateMemberDto;
import com.soha.librarymanagement.dto.MemberDto;
import com.soha.librarymanagement.dto.MemberResponse;
import com.soha.librarymanagement.dto.MemberSearchCriteria;
import com.soha.librarymanagement.errorhandling.ApiResponse;
import com.soha.librarymanagement.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','STAFF')")
public class MemberController {

    private final MemberService service;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody CreateMemberDto body,
                                                 @AuthenticationPrincipal String username
    ) {
        MemberResponse created = service.create(body, username);
        return ResponseEntity.status(201)
                .body(ApiResponse.success(created, "Member created successfully", 201));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> get(@PathVariable Integer id) {
        MemberResponse response = service.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Member retrieved successfully", 200));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<?>> search(MemberSearchCriteria criteria, Pageable pageable) {
        Page<MemberResponse> page = service.search(criteria, pageable);
        return ResponseEntity.ok(ApiResponse.success(page, "Members fetched successfully", 200));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable Integer id, @Valid @RequestBody MemberDto body) {
        MemberResponse updated = service.update(id, body);
        return ResponseEntity.ok(ApiResponse.success(updated, "Member updated successfully", 200));
    }

    @PatchMapping("/deactivate/{id}")
    public ResponseEntity<ApiResponse<?>> deactivate(@PathVariable Integer id) {
        service.deactivate(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Member deactivated", 200));
    }
}
