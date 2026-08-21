package com.soares.banking_api.service;

import com.soares.banking_api.dto.*;
import com.soares.banking_api.entity.Account;
import com.soares.banking_api.entity.Customer;
import com.soares.banking_api.entity.Transaction;
import com.soares.banking_api.entity.TransactionCategory;
import com.soares.banking_api.exception.AccountNotFoundException;
import com.soares.banking_api.exception.CustomerNotFoundException;
import com.soares.banking_api.exception.InsufficientBalanceException;
import com.soares.banking_api.exception.SameAccountTransferException;
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
    private final IdempotencyService idempotencyService;

    public AccountService(AccountRepository accountRepository, CustomerRepository customerRepository, TransactionRepository transactionRepository, TransactionCategoryRepository transactionCategoryRepository, IdempotencyService idempotencyService) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
        this.transactionCategoryRepository = transactionCategoryRepository;
        this.idempotencyService = idempotencyService;

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

    public List <AccountResponse> findAll(){
        return  accountRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AccountResponse findById(Long id) {

        Account account = findAccountById(id);

        return toResponse(account);
    }

    @Transactional
    public AccountResponse deposit(Long accountId, DepositRequest request, String idempotencyKey){

        idempotencyService.reserve(idempotencyKey);

        Account account = findAccountById(accountId);

        TransactionCategory category = findCategoryByName("DEPOSIT");
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

    @Transactional
    public AccountResponse withdraw(Long accountId, WithdrawRequest request, String idempotencyKey) {

        idempotencyService.reserve(idempotencyKey);

        Account account = findAccountById(accountId);

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException();
        }

        TransactionCategory category = findCategoryByName("WITHDRAW");

        account.setBalance(
                account.getBalance().subtract(request.getAmount())
        );

        accountRepository.save(account);

        Transaction transaction = new Transaction(
                request.getAmount(),
                category,
                account,
                null
        );

        transactionRepository.save(transaction);

        return toResponse(account);
    }

    @Transactional
    public AccountResponse transfer(Long sourceAccountId, TransferRequest request, String idempotencyKey) {

        idempotencyService.reserve(idempotencyKey);

        Account sourceAccount = findAccountById(sourceAccountId);

        Account destinationAccount =
                findAccountById(request.getDestinationAccountId());

        if (sourceAccount.getId().equals(destinationAccount.getId())) {
            throw new SameAccountTransferException();
        }

        if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException();
        }

        TransactionCategory category = findCategoryByName("TRANSFER");

        sourceAccount.setBalance(
                sourceAccount.getBalance().subtract(request.getAmount())
        );

        destinationAccount.setBalance(
                destinationAccount.getBalance().add(request.getAmount())
        );

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        Transaction transaction = new Transaction(
                request.getAmount(),
                category,
                sourceAccount,
                destinationAccount
        );

        transactionRepository.save(transaction);

        return toResponse(sourceAccount);
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
    private Account findAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }
    private TransactionCategory findCategoryByName(String name) {
        return transactionCategoryRepository
                .findByName(name)
                .orElseThrow();
    }

}