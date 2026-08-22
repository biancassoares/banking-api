package com.soares.banking_api.controller;

import com.soares.banking_api.dto.LoginRequest;
import com.soares.banking_api.dto.LoginResponse;
import com.soares.banking_api.entity.Customer;
import com.soares.banking_api.service.AuthService;
import com.soares.banking_api.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        Customer customer = authService.authenticate(request);

        String token = jwtService.generateToken(customer);

        return ResponseEntity.ok(new LoginResponse(token));
    }
}
