package com.soares.banking_api.service;

import com.soares.banking_api.dto.LoginRequest;
import com.soares.banking_api.entity.Customer;
import com.soares.banking_api.exception.InvalidCredentialsException;
import com.soares.banking_api.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder) {

        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Customer authenticate(LoginRequest request) {

        Customer customer = customerRepository
                .findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                customer.getPassword()
        );

        if (!passwordMatches) {
            throw new InvalidCredentialsException();
        }

        return customer;
    }
}
