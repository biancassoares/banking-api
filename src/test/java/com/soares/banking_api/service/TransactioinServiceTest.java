package com.soares.banking_api.service;

import com.soares.banking_api.dto.TransactionResponse;
import com.soares.banking_api.entity.Account;
import com.soares.banking_api.entity.Transaction;
import com.soares.banking_api.entity.TransactionCategory;
import com.soares.banking_api.exception.AccountNotFoundException;
import com.soares.banking_api.repository.AccountRepository;
import com.soares.banking_api.repository.TransactionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
class TransactioinServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService(
                transactionRepository,
                accountRepository
        );
    }
    @Test
    void shouldFindTransactionsByAccount() {

        Account account = new Account();
        account.setId(1L);

        TransactionCategory category = new TransactionCategory();
        category.setName("DEPOSIT");

        Transaction transaction = new Transaction(
                new BigDecimal("100.00"),
                category,
                null,
                account
        );

        Pageable pageable = PageRequest.of(0, 10);

        Page<Transaction> transactionPage =
                new PageImpl<>(List.of(transaction));

        when(accountRepository.existsById(1L))
                .thenReturn(true);

        when(transactionRepository
                .findBySourceAccountIdOrDestinationAccountId(
                        1L,
                        1L,
                        pageable
                ))
                .thenReturn(transactionPage);

        Page<TransactionResponse> response =
                transactionService.findByAccount(
                        1L,
                        null,
                        null,
                        null,
                        pageable
                );

        assertEquals(1, response.getTotalElements());
        assertEquals(
                new BigDecimal("100.00"),
                response.getContent().get(0).getAmount()
        );
        assertEquals(
                "DEPOSIT",
                response.getContent().get(0).getCategory()
        );
    }

    @Test
    void shouldThrowWhenAccountDoesNotExist(){

        Pageable pageable = PageRequest.of(0, 10);

        when(accountRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(
                AccountNotFoundException.class,
                () -> transactionService.findByAccount(
                        1L,
                        null,
                        null,
                        null,
                        pageable
                )
        );
    }

    @Test
    void shouldFilterTransactionsByCategory() {

        Account account = new Account();
        account.setId(1L);

        TransactionCategory category = new TransactionCategory();
        category.setName("DEPOSIT");

        Transaction transaction = new Transaction(
                new BigDecimal("100.00"),
                category,
                null,
                account
        );

        Pageable pageable = PageRequest.of(0, 10);

        Page<Transaction> transactionPage =
                new PageImpl<>(List.of(transaction));

        when(accountRepository.existsById(1L))
                .thenReturn(true);

        when(transactionRepository.findByAccountAndCategory(
                1L,
                "DEPOSIT",
                pageable
        )).thenReturn(transactionPage);

        Page<TransactionResponse> response =
                transactionService.findByAccount(
                        1L,
                        "DEPOSIT",
                        null,
                        null,
                        pageable
                );

        assertEquals(1, response.getTotalElements());

        assertEquals(
                "DEPOSIT",
                response.getContent().get(0).getCategory()
        );

        assertEquals(
                new BigDecimal("100.00"),
                response.getContent().get(0).getAmount()
        );
    }
    @Test
    void shouldFilterTransactionsByPeriod() {

        Account account = new Account();
        account.setId(1L);

        TransactionCategory category = new TransactionCategory();
        category.setName("DEPOSIT");

        Transaction transaction = new Transaction(
                new BigDecimal("100.00"),
                category,
                null,
                account
        );

        LocalDateTime startDate =
                LocalDateTime.of(2026, 8, 1, 0, 0);

        LocalDateTime endDate =
                LocalDateTime.of(2026, 8, 31, 23, 59);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Transaction> transactionPage =
                new PageImpl<>(List.of(transaction));

        when(accountRepository.existsById(1L))
                .thenReturn(true);

        when(transactionRepository.findByAccountAndPeriod(
                1L,
                startDate,
                endDate,
                pageable
        )).thenReturn(transactionPage);

        Page<TransactionResponse> response =
                transactionService.findByAccount(
                        1L,
                        null,
                        startDate,
                        endDate,
                        pageable
                );

        assertEquals(1, response.getTotalElements());

        assertEquals(
                new BigDecimal("100.00"),
                response.getContent().get(0).getAmount()
        );
    }
    @Test
    void shouldFilterTransactionsByCategoryAndPeriod() {

        Account account = new Account();
        account.setId(1L);

        TransactionCategory category = new TransactionCategory();
        category.setName("TRANSFER");

        Transaction transaction = new Transaction(
                new BigDecimal("100.00"),
                category,
                account,
                null
        );

        LocalDateTime startDate =
                LocalDateTime.of(2026, 8, 1, 0, 0);

        LocalDateTime endDate =
                LocalDateTime.of(2026, 8, 31, 23, 59);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Transaction> transactionPage =
                new PageImpl<>(List.of(transaction));

        when(accountRepository.existsById(1L))
                .thenReturn(true);

        when(transactionRepository.findByAccountCategoryAndPeriod(
                1L,
                "TRANSFER",
                startDate,
                endDate,
                pageable
        )).thenReturn(transactionPage);

        Page<TransactionResponse> response =
                transactionService.findByAccount(
                        1L,
                        "TRANSFER",
                        startDate,
                        endDate,
                        pageable
                );

        assertEquals(1, response.getTotalElements());

        assertEquals(
                "TRANSFER",
                response.getContent().get(0).getCategory()
        );

        assertEquals(
                new BigDecimal("100.00"),
                response.getContent().get(0).getAmount()
        );
    }
    @Test
    void shouldThrowWhenOnlyStartDateIsProvided() {

        LocalDateTime startDate =
                LocalDateTime.of(2026, 8, 1, 0, 0);

        Pageable pageable = PageRequest.of(0, 10);

        when(accountRepository.existsById(1L))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.findByAccount(
                        1L,
                        null,
                        startDate,
                        null,
                        pageable
                )
        );
    }
    @Test
    void shouldThrowWhenOnlyEndDateIsProvided() {

        LocalDateTime endDate =
                LocalDateTime.of(2026, 8, 31, 23, 59);

        Pageable pageable = PageRequest.of(0, 10);

        when(accountRepository.existsById(1L))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.findByAccount(
                        1L,
                        null,
                        null,
                        endDate,
                        pageable
                )
        );
    }

    @Test
    void shouldThrowWhenStartDateIsAfterEndDate() {

        LocalDateTime startDate =
                LocalDateTime.of(2026, 8, 31, 0, 0);

        LocalDateTime endDate =
                LocalDateTime.of(2026, 8, 1, 0, 0);

        Pageable pageable = PageRequest.of(0, 10);

        when(accountRepository.existsById(1L))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.findByAccount(
                        1L,
                        null,
                        startDate,
                        endDate,
                        pageable
                )
        );
    }


}
