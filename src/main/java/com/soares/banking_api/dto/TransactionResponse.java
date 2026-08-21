package com.soares.banking_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TransactionResponse {

    private Long id;
    private BigDecimal amount;
    private String category;
    private LocalDateTime createdAt;
    private Long sourceAccountId;
    private Long destinationAccountId;


}
