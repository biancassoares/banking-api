package com.soares.banking_api.controller;

import com.soares.banking_api.dto.AccountRequest;
import com.soares.banking_api.dto.AccountResponse;
import com.soares.banking_api.dto.DepositRequest;
import com.soares.banking_api.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> create(
            @Valid @RequestBody AccountRequest request) {

        AccountResponse response = accountService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> findAll() {
        return ResponseEntity.ok(accountService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.findById(id));
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<AccountResponse> deposit(
            @PathVariable Long id,
            @Valid @RequestBody DepositRequest request) {

        return ResponseEntity.ok(accountService.deposit(id, request));
    }
}