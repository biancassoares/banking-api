package com.soares.banking_api.service;

import com.soares.banking_api.dto.CustomerRequest;
import com.soares.banking_api.dto.CustomerResponse;
import com.soares.banking_api.entity.Customer;
import com.soares.banking_api.exception.CpfAlreadyExistsException;
import com.soares.banking_api.exception.CustomerNotFoundException;
import com.soares.banking_api.exception.EmailAlreadyExistsException;
import com.soares.banking_api.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;

    }

    public CustomerResponse create(CustomerRequest request) {

        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException();
        }

        if (customerRepository.existsByCpf(request.getCpf())) {
            throw new CpfAlreadyExistsException();
        }
        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        Customer customer = new Customer(
                request.getName(),
                request.getEmail(),
                request.getCpf(),
                encodedPassword

        );

        Customer savedCustomer = customerRepository.save(customer);

        return toResponse(savedCustomer);
    }

    public List<CustomerResponse> findAll() {
        return customerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }
    public CustomerResponse findById(Long id) {

        Customer customer = findCustomerById(id);

        return toResponse(customer);
    }

    public CustomerResponse update(Long id, CustomerRequest request) {

        Customer customer = findCustomerById(id);

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setCpf(request.getCpf());

        Customer updatedCustomer = customerRepository.save(customer);

        return toResponse(updatedCustomer);
    }

    public void delete(Long id) {

        Customer customer = findCustomerById(id);

        customerRepository.delete(customer);
    }
    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getCpf()
        );
    }
    private Customer findCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

}
