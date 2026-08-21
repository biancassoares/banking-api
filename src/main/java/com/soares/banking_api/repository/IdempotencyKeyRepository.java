package com.soares.banking_api.repository;

import com.soares.banking_api.entity.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyKeyRepository
        extends JpaRepository<IdempotencyKey, Long> {

    boolean existsByKey(String key);
}
