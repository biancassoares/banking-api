package com.soares.banking_api.config;

import com.soares.banking_api.entity.TransactionCategory;
import com.soares.banking_api.repository.TransactionCategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final TransactionCategoryRepository transactionCategoryRepository;

    public DataInitializer(TransactionCategoryRepository transactionCategoryRepository) {
        this.transactionCategoryRepository = transactionCategoryRepository;
    }

    @Override
    public void run(String... args) {

        createCategoryIfNotExists(
                "DEPOSIT",
                "Deposit into account"
        );

        createCategoryIfNotExists(
                "WITHDRAW",
                "Withdrawal from account"
        );

        createCategoryIfNotExists(
                "TRANSFER",
                "Transfer between accounts"
        );
    }

    private void createCategoryIfNotExists(String name, String description) {

        if (transactionCategoryRepository.findByName(name).isEmpty()) {

            TransactionCategory category =
                    new TransactionCategory(name, description);

            transactionCategoryRepository.save(category);
        }
    }
}