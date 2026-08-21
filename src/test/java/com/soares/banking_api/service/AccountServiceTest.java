package com.soares.banking_api.service;

import com.soares.banking_api.dto.*;
import com.soares.banking_api.entity.Account;
import com.soares.banking_api.entity.Customer;
import com.soares.banking_api.entity.TransactionCategory;
import com.soares.banking_api.exception.*;
import com.soares.banking_api.repository.AccountRepository;
import com.soares.banking_api.repository.CustomerRepository;
import com.soares.banking_api.repository.TransactionCategoryRepository;
import com.soares.banking_api.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionCategoryRepository transactionCategoryRepository;

    private AccountService accountService;
    @Mock
    private IdempotencyService idempotencyService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(
                accountRepository,
                customerRepository,
                transactionRepository,
                transactionCategoryRepository,
                idempotencyService
        );
    }

    @Test
    void shouldCreateAccountWhenCustomerExists() {

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Bianca");

        AccountRequest request = new AccountRequest();
        request.setCustomerId(1L);

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(accountRepository.existsByAccountNumber(anyString()))
                .thenReturn(false);

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AccountResponse response = accountService.create(request);

        assertEquals(1L, response.getCustomerId());
        assertEquals("Bianca", response.getCustomerName());
        assertTrue(response.getAccountNumber().startsWith("ACC-"));
    }

    @Test
    void shouldThrowWhenCustomerDoesNotExist() {

        AccountRequest request = new AccountRequest();
        request.setCustomerId(1L);

        when(customerRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> accountService.create(request)
        );
    }

    @Test
    void shouldFindAccountById(){
        Customer customer = new Customer();
        customer.setId(1L);
        Account account = new Account();
        account.setId(1L);
        account.setCustomer(customer);

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        AccountResponse response = accountService.findById(1L);

        assertEquals(1L, response.getId());

    }

    @Test
    void shouldThrowWhenAccountDoesNotExist() {

        when(accountRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.findById(1L)
        );
    }
    @Test
    void shouldDepositSuccessfully() {

        Customer customer = new Customer();

        Account account = new Account();
        account.setId(1L);
        account.setCustomer(customer);
        account.setBalance(new BigDecimal("100.00"));

        DepositRequest request = new DepositRequest();
        request.setAmount(new BigDecimal("50.00"));

        TransactionCategory category = new TransactionCategory();

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        when(transactionCategoryRepository.findByName("DEPOSIT"))
                .thenReturn(Optional.of(category));

        AccountResponse response = accountService.deposit(1L, request, "test-key");

        assertEquals(new BigDecimal("150.00"), response.getBalance());

        verify(idempotencyService).reserve("test-key");
    }

    @Test
    void shouldWithdrawWhenBalanceIsSufficient(){

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Bianca");

        Account account = new Account();
        account.setCustomer(customer);
        account.setBalance(new BigDecimal("200.00"));

        WithdrawRequest request =  new WithdrawRequest();
        request.setAmount(new BigDecimal("50.00"));

        TransactionCategory category = new TransactionCategory();

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        when(transactionCategoryRepository.findByName("WITHDRAW"))
                .thenReturn(Optional.of(category));

        AccountResponse response = accountService.withdraw(1L, request, "test-key");

        assertEquals(new BigDecimal("150.00"), response.getBalance());

        verify(idempotencyService).reserve("test-key");
    }

    @Test
    void shouldThrowWhenWithdrawBalanceIsInsufficient(){

        Account account = new Account();
        account.setBalance(new BigDecimal("50.00"));

        WithdrawRequest request =  new WithdrawRequest();
        request.setAmount(new BigDecimal("100.00"));

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        assertThrows(InsufficientBalanceException.class, ()-> accountService.withdraw(1L,request, "test-key"));

    }
    @Test
    void shouldTransferSuccessfully(){

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Bianca");

        Customer destinationCustomer = new Customer();
        destinationCustomer.setId(2L);
        destinationCustomer.setName("Joao");

        Account sourceAccount = new Account();
        sourceAccount.setId(1L);
        sourceAccount.setBalance(new BigDecimal("200.00"));
        sourceAccount.setCustomer(customer);

        Account destinationAccount = new Account();
        destinationAccount.setId(2L);
        destinationAccount.setBalance(new BigDecimal("100.00"));
        destinationAccount.setCustomer(destinationCustomer);

        TransferRequest request = new TransferRequest();
        request.setAmount(new BigDecimal("50.00"));
        request.setDestinationAccountId(2L);

        TransactionCategory category = new TransactionCategory();

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(sourceAccount));

        when(accountRepository.findById(2L))
                .thenReturn(Optional.of(destinationAccount));

        when(transactionCategoryRepository.findByName("TRANSFER"))
                .thenReturn(Optional.of(category));

        AccountResponse response = accountService.transfer(1L, request, "test-key");

        assertEquals(
                new BigDecimal("150.00"),
                response.getBalance()
        );

        assertEquals(
                new BigDecimal("150.00"),
                destinationAccount.getBalance()
        );

        verify(idempotencyService).reserve("test-key");
    }

    @Test
    void shouldThrowWhenTransferBalanceIsInsufficient(){

        Account sourceAccount = new Account();
        sourceAccount.setId(1L);
        sourceAccount.setBalance(new BigDecimal("50.00"));

        Account destinationAccount = new Account();
        destinationAccount.setId(2L);

        TransferRequest request = new TransferRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setDestinationAccountId(2L);

        when (accountRepository.findById(1L))
                .thenReturn(Optional.of(sourceAccount));

        when(accountRepository.findById(2L))
                .thenReturn(Optional.of(destinationAccount));

        assertThrows(InsufficientBalanceException.class,()->accountService.transfer(1L, request, "test-key"));
    }

    @Test
    void shouldThrowWhenTransferIsToSameAccount(){

        Account account = new Account();
        account.setId(1L);
        account.setBalance(new BigDecimal("50.00"));

        TransferRequest request = new TransferRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setDestinationAccountId(1L);

        when (accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        assertThrows(SameAccountTransferException.class,()-> accountService.transfer(1L, request, "test-key"));

    }

    @Test
    void shouldNotDepositWhenIdempotencyKeyAlreadyExists() {

        DepositRequest request = new DepositRequest();
        request.setAmount(new BigDecimal("100.00"));

        doThrow(new IdempotencyKeyAlreadyExistsException())
                .when(idempotencyService)
                .reserve("duplicate-key");

        assertThrows(
                IdempotencyKeyAlreadyExistsException.class,
                () -> accountService.deposit(1L, request, "duplicate-key")
        );

        verify(accountRepository, never()).findById(anyLong());
        verify(transactionRepository, never()).save(any());
    }
}
