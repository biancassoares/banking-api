package com.soares.banking_api.repository;

import com.soares.banking_api.entity.TransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionCategoryRepository extends JpaRepository<TransactionCategory, Long> {

    Optional<TransactionCategory> findByName(String name);


}
