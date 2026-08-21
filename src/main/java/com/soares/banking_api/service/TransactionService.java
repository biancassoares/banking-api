package com.soares.banking_api.service;

import com.soares.banking_api.dto.TransactionResponse;
import com.soares.banking_api.entity.Transaction;
import com.soares.banking_api.exception.AccountNotFoundException;
import com.soares.banking_api.repository.AccountRepository;
import com.soares.banking_api.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public Page<TransactionResponse> findByAccount(
            Long accountId,
            String category,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    ) {

        if (!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException(accountId);
        }

        validatePeriod(startDate, endDate);

        if (category != null && startDate != null && endDate != null) {
            return transactionRepository
                    .findByAccountCategoryAndPeriod(
                            accountId,
                            category.toUpperCase(),
                            startDate,
                            endDate,
                            pageable
                    )
                    .map(this::toResponse);
        }

        if (startDate != null && endDate != null) {
            return transactionRepository
                    .findByAccountAndPeriod(
                            accountId,
                            startDate,
                            endDate,
                            pageable
                    )
                    .map(this::toResponse);
        }

        if (category != null) {
            return transactionRepository
                    .findByAccountAndCategory(
                            accountId,
                            category.toUpperCase(),
                            pageable
                    )
                    .map(this::toResponse);
        }

        return transactionRepository
                .findBySourceAccountIdOrDestinationAccountId(
                        accountId,
                        accountId,
                        pageable
                )
                .map(this::toResponse);
    }
    private TransactionResponse toResponse(Transaction transaction) {

        Long sourceAccountId = transaction.getSourceAccount() != null
                ? transaction.getSourceAccount().getId()
                : null;

        Long destinationAccountId = transaction.getDestinationAccount() != null
                ? transaction.getDestinationAccount().getId()
                : null;

        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getCategory().getName(),
                transaction.getCreatedAt(),
                sourceAccountId,
                destinationAccountId
        );
    }
    private void validatePeriod(
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {

        if ((startDate == null && endDate != null)
                || (startDate != null && endDate == null)) {
            throw new IllegalArgumentException(
                    "Start date and end date must be provided together"
            );
        }

        if (startDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date"
            );
        }
    }
}