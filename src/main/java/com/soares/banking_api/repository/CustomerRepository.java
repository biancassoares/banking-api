package com.soares.banking_api.repository;

import com.soares.banking_api.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    Optional<Customer> findByEmail(String email);
}