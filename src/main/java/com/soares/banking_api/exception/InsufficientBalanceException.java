package com.soares.banking_api.exception;

public class InsufficientBalanceException extends  RuntimeException{
    public InsufficientBalanceException(){
        super ("Insufficient balance");
    }
}
