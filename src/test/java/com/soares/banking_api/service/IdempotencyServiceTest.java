package com.soares.banking_api.service;

import com.soares.banking_api.entity.IdempotencyKey;
import com.soares.banking_api.exception.IdempotencyKeyAlreadyExistsException;
import com.soares.banking_api.repository.IdempotencyKeyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdempotencyServiceTest {

    @Mock
    private IdempotencyKeyRepository idempotencyKeyRepository;

    private IdempotencyService idempotencyService;

    @BeforeEach
    void setUp() {
        idempotencyService = new IdempotencyService(idempotencyKeyRepository);
    }

    @Test
    void shouldReserveIdempotencyKeySuccessfully() {

        idempotencyService.reserve("test-key");

        verify(idempotencyKeyRepository)
                .saveAndFlush(any(IdempotencyKey.class));
    }
    @Test
    void shouldThrowWhenIdempotencyKeyIsBlank() {

        assertThrows(
                IllegalArgumentException.class,
                () -> idempotencyService.reserve("")
        );
    }
    @Test
    void shouldThrowWhenIdempotencyKeyAlreadyExists() {

        when(idempotencyKeyRepository.saveAndFlush(any(IdempotencyKey.class)))
                .thenThrow(DataIntegrityViolationException.class);

        assertThrows(
                IdempotencyKeyAlreadyExistsException.class,
                () -> idempotencyService.reserve("duplicate-key")
        );
    }
}