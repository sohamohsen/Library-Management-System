package com.soha.librarymanagement.controller;

import com.soha.librarymanagement.dto.*;
import com.soha.librarymanagement.service.LoanService;
import com.soha.librarymanagement.errorhandling.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService service;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody CreateLoanRequest req,
                                                 @AuthenticationPrincipal String username
    ) {
        var res = service.create(req, username);
        return ResponseEntity.status(201).body(ApiResponse.success(res, "Loan created", 201));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> get(@PathVariable Integer id) {
        var res = service.get(id);
        return ResponseEntity.ok(ApiResponse.success(res, "Loan fetched", 200));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<?>> search(LoanSearchCriteria criteria, Pageable pageable) {
        Page<LoanResponse> page = service.search(criteria, pageable);
        return ResponseEntity.ok(ApiResponse.success(page, "Loans fetched", 200));
    }


    @PatchMapping("/{id}/items/add")
    public ResponseEntity<ApiResponse<?>> addItems(@PathVariable Integer id, @Valid @RequestBody AddLoanItemsRequest req) {
        var res = service.addItems(id, req);
        return ResponseEntity.ok(ApiResponse.success(res, "Items added", 200));
    }

    @PatchMapping("/{id}/items/return")
    public ResponseEntity<ApiResponse<?>> returnItems(@PathVariable Integer id, @Valid @RequestBody LoanItemReturnRequest req) {
        var res = service.returnItems(id, req);
        return ResponseEntity.ok(ApiResponse.success(res, "Items returned", 200));
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<ApiResponse<?>> close(@PathVariable Integer id) {
        var res = service.close(id);
        return ResponseEntity.ok(ApiResponse.success(res, "Loan closed", 200));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<?>> cancel(@PathVariable Integer id) {
        var res = service.cancel(id);
        return ResponseEntity.ok(ApiResponse.success(res, "Loan cancelled", 200));
    }

    private Page<?> servicePageAdapter(Pageable pageable) {
        return Page.empty(pageable);
    }
}