package com.soares.banking_api.exception;

public class CpfAlreadyExistsException extends RuntimeException{
    public CpfAlreadyExistsException() {
        super("CPF already registered");
    }
}
