package com.soares.banking_api.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountRequest {

    @NotNull (message = "Customer id is required")
    private Long customerId;


}
