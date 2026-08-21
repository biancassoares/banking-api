package com.soares.banking_api.repository;

import com.soares.banking_api.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findBySourceAccountIdOrDestinationAccountId(
            Long sourceAccountId,
            Long destinationAccountId,
            Pageable pageable
    );
    @Query("""
        SELECT t FROM Transaction t
        WHERE (t.sourceAccount.id = :accountId
               OR t.destinationAccount.id = :accountId)
        AND t.category.name = :category
        """)
    Page<Transaction> findByAccountAndCategory(
            @Param("accountId") Long accountId,
            @Param("category") String category,
            Pageable pageable
    );

    @Query("""
        SELECT t FROM Transaction t
        WHERE (t.sourceAccount.id = :accountId
               OR t.destinationAccount.id = :accountId)
        AND t.createdAt BETWEEN :startDate AND :endDate
        """)
    Page<Transaction> findByAccountAndPeriod(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("""
        SELECT t FROM Transaction t
        WHERE (t.sourceAccount.id = :accountId
               OR t.destinationAccount.id = :accountId)
        AND t.category.name = :category
        AND t.createdAt BETWEEN :startDate AND :endDate
        """)
    Page<Transaction> findByAccountCategoryAndPeriod(
            @Param("accountId") Long accountId,
            @Param("category") String category,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}
