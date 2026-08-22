package com.soares.banking_api.service;

import com.soares.banking_api.dto.LoginRequest;
import com.soares.banking_api.entity.Customer;
import com.soares.banking_api.exception.InvalidCredentialsException;
import com.soares.banking_api.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                customerRepository,
                passwordEncoder
        );
    }

    @Test
    void shouldAuthenticateSuccessfully() {

        LoginRequest request = new LoginRequest();
        request.setEmail("bianca@email.com");
        request.setPassword("123456");

        Customer customer = new Customer();
        customer.setEmail("bianca@email.com");
        customer.setPassword("encoded-password");

        when(customerRepository.findByEmail("bianca@email.com"))
                .thenReturn(Optional.of(customer));

        when(passwordEncoder.matches(
                "123456",
                "encoded-password"
        )).thenReturn(true);

        Customer result = authService.authenticate(request);

        assertEquals("bianca@email.com", result.getEmail());

        verify(passwordEncoder)
                .matches("123456", "encoded-password");
    }

    @Test
    void shouldThrowWhenEmailDoesNotExist() {

        LoginRequest request = new LoginRequest();
        request.setEmail("notfound@email.com");
        request.setPassword("123456");

        when(customerRepository.findByEmail("notfound@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.authenticate(request)
        );
    }

    @Test
    void shouldThrowWhenPasswordIsIncorrect() {

        LoginRequest request = new LoginRequest();
        request.setEmail("bianca@email.com");
        request.setPassword("wrong-password");

        Customer customer = new Customer();
        customer.setEmail("bianca@email.com");
        customer.setPassword("encoded-password");

        when(customerRepository.findByEmail("bianca@email.com"))
                .thenReturn(Optional.of(customer));

        when(passwordEncoder.matches(
                "wrong-password",
                "encoded-password"
        )).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.authenticate(request)
        );
    }
}
