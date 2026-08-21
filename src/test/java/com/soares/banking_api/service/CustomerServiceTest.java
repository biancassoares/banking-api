package com.soares.banking_api.service;

import com.soares.banking_api.dto.CustomerRequest;
import com.soares.banking_api.dto.CustomerResponse;
import com.soares.banking_api.entity.Customer;
import com.soares.banking_api.exception.CpfAlreadyExistsException;
import com.soares.banking_api.exception.CustomerNotFoundException;
import com.soares.banking_api.exception.EmailAlreadyExistsException;
import com.soares.banking_api.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;

    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService(customerRepository);
    }

    @Test
    void shouldCreateCustomer() {

        CustomerRequest request = new CustomerRequest();
        request.setName("Bianca");
        request.setEmail("bianca@email.com");
        request.setCpf("11111111111");

        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(invocation -> {
                    Customer savedCustomer = invocation.getArgument(0);
                    savedCustomer.setId(1L);
                    return savedCustomer;
                });

        CustomerResponse response = customerService.create(request);

        assertEquals(1L, response.getId());
        assertEquals("Bianca", response.getName());
        assertEquals("bianca@email.com", response.getEmail());
        assertEquals("11111111111", response.getCpf());
    }
    @Test
    void shouldFindCustomerById() {

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Bianca");
        customer.setEmail("bianca@email.com");
        customer.setCpf("11111111111");

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        CustomerResponse response = customerService.findById(1L);

        assertEquals(1L, response.getId());
        assertEquals("Bianca", response.getName());
        assertEquals("bianca@email.com", response.getEmail());
        assertEquals("11111111111", response.getCpf());
    }
    @Test
    void shouldThrowWhenCustomerDoesNotExist() {

        when(customerRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.findById(1L)
        );
    }
    @Test
    void shouldFindAllCustomers() {

        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setName("Bianca");
        customer1.setEmail("bianca@email.com");
        customer1.setCpf("11111111111");

        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setName("Joao");
        customer2.setEmail("joao@email.com");
        customer2.setCpf("22222222222");

        when(customerRepository.findAll())
                .thenReturn(List.of(customer1, customer2));

        List<CustomerResponse> response =
                customerService.findAll();

        assertEquals(2, response.size());
        assertEquals("Bianca", response.get(0).getName());
        assertEquals("Joao", response.get(1).getName());
    }

    @Test
    void shouldUpdateCustomer() {

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Bianca");
        customer.setEmail("bianca@email.com");
        customer.setCpf("11111111111");

        CustomerRequest request = new CustomerRequest();
        request.setName("Bianca Soares");
        request.setEmail("bianca.soares@email.com");
        request.setCpf("22222222222");

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CustomerResponse response =
                customerService.update(1L, request);

        assertEquals("Bianca Soares", response.getName());
        assertEquals("bianca.soares@email.com", response.getEmail());
        assertEquals("22222222222", response.getCpf());
    }
    @Test
    void shouldDeleteCustomer() {

        Customer customer = new Customer();
        customer.setId(1L);

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        customerService.delete(1L);

        verify(customerRepository).delete(customer);
    }
    @Test
    void shouldThrowWhenDeletingCustomerDoesNotExist() {

        when(customerRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.delete(1L)
        );
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {

        CustomerRequest request = new CustomerRequest();
        request.setEmail("test@email.com");
        request.setCpf("12345678900");

        when(customerRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> customerService.create(request)
        );
    }

    @Test
    void shouldThrowWhenCpfAlreadyExists() {

        CustomerRequest request = new CustomerRequest();
        request.setEmail("test@email.com");
        request.setCpf("12345678900");

        when(customerRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(customerRepository.existsByCpf(request.getCpf()))
                .thenReturn(true);

        assertThrows(
                CpfAlreadyExistsException.class,
                () -> customerService.create(request)
        );
    }

}
