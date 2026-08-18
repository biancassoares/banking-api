package com.soares.banking_api.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class AccountResponse {

    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private  Long customerId;
    private String customerName;

}
