package com.soares.banking_api.service;

import com.soares.banking_api.dto.CustomerRequest;
import com.soares.banking_api.dto.CustomerResponse;
import com.soares.banking_api.entity.Customer;
import com.soares.banking_api.exception.CustomerNotFoundException;
import com.soares.banking_api.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponse create(CustomerRequest request) {

        Customer customer = new Customer(
                request.getName(),
                request.getEmail(),
                request.getCpf()
        );

        Customer savedCustomer = customerRepository.save(customer);

        return toResponse(savedCustomer);
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getCpf()
        );
    }

    public List<CustomerResponse> findAll() {
        return customerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }
    public CustomerResponse findById(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        return toResponse(customer);
    }

    public CustomerResponse update(Long id, CustomerRequest request) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setCpf(request.getCpf());

        Customer updatedCustomer = customerRepository.save(customer);

        return toResponse(updatedCustomer);
    }

    public void delete(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        customerRepository.delete(customer);
    }




}
