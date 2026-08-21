package com.soares.banking_api.service;

import com.soares.banking_api.entity.IdempotencyKey;
import com.soares.banking_api.exception.IdempotencyKeyAlreadyExistsException;
import com.soares.banking_api.repository.IdempotencyKeyRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class IdempotencyService {

    private final IdempotencyKeyRepository idempotencyKeyRepository;

    public IdempotencyService(IdempotencyKeyRepository idempotencyKeyRepository) {
        this.idempotencyKeyRepository = idempotencyKeyRepository;
    }

    public void reserve(String key) {

        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Idempotency-Key cannot be blank");
        }

        try {
            IdempotencyKey idempotencyKey = new IdempotencyKey(key);

            idempotencyKeyRepository.saveAndFlush(idempotencyKey);

        } catch (DataIntegrityViolationException exception) {
            throw new IdempotencyKeyAlreadyExistsException();
        }
    }
}
