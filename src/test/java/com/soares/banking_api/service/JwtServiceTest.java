package com.soares.banking_api.service;

import com.soares.banking_api.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
                "banking-api-development-secret-key-2026-secure",
                3600000
        );
    }
    @Test
    void shouldGenerateValidToken() {

        Customer customer = new Customer();
        customer.setEmail("bianca@email.com");

        String token = jwtService.generateToken(customer);

        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token));
    }
    @Test
    void shouldExtractEmailFromToken() {

        Customer customer = new Customer();
        customer.setEmail("bianca@email.com");

        String token = jwtService.generateToken(customer);

        String email = jwtService.extractEmail(token);

        assertEquals("bianca@email.com", email);
    }
    @Test
    void shouldReturnFalseForInvalidToken() {

        boolean result = jwtService.isTokenValid("invalid-token");

        assertFalse(result);
    }
}