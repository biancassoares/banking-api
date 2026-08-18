package com.soares.banking_api.service;

import com.soares.banking_api.dto.AccountRequest;
import com.soares.banking_api.dto.AccountResponse;
import com.soares.banking_api.dto.DepositRequest;
import com.soares.banking_api.entity.Account;
import com.soares.banking_api.entity.Customer;
import com.soares.banking_api.entity.Transaction;
import com.soares.banking_api.entity.TransactionCategory;
import com.soares.banking_api.exception.AccountNotFoundException;
import com.soares.banking_api.exception.CustomerNotFoundException;
import com.soares.banking_api.repository.AccountRepository;
import com.soares.banking_api.repository.CustomerRepository;
import com.soares.banking_api.repository.TransactionCategoryRepository;
import com.soares.banking_api.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionCategoryRepository transactionCategoryRepository;

    public AccountService(AccountRepository accountRepository, CustomerRepository customerRepository, TransactionRepository transactionRepository, TransactionCategoryRepository transactionCategoryRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
        this.transactionCategoryRepository = transactionCategoryRepository;
    }



    public AccountResponse create(AccountRequest request) {

        Customer customer = customerRepository
                .findById(request.getCustomerId())
                .orElseThrow(() ->
                        new CustomerNotFoundException(request.getCustomerId()));

        String accountNumber = generateAccountNumber();

        Account account = new Account(
                accountNumber,
                customer
        );

        Account savedAccount = accountRepository.save(account);

        return toResponse(savedAccount);
    }

    private AccountResponse toResponse(Account account) {

        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getCustomer().getId(),
                account.getCustomer().getName()
        );
    }

    private String generateAccountNumber() {

        String accountNumber;

        do {
            accountNumber = "ACC-" + UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase();

        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    public List <AccountResponse> findAll(){
        return  accountRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AccountResponse findById(Long id) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        return toResponse(account);
    }

    @Transactional
    public AccountResponse deposit(Long accountId, DepositRequest request){

        Account account = accountRepository.findById(accountId)
                .orElseThrow(()-> new AccountNotFoundException(accountId));

        TransactionCategory category = transactionCategoryRepository
                .findByName("DEPOSIT")
                .orElseThrow();
        account.setBalance(
                account.getBalance().add(request.getAmount())
        );

        accountRepository.save(account);

        Transaction transaction = new Transaction(
                request.getAmount(),
                category,
                null,
                account
        );

        transactionRepository.save(transaction);

        return toResponse(account);
    }





}