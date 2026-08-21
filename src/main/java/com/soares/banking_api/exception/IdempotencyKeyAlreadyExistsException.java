package com.soares.banking_api.exception;

public class IdempotencyKeyAlreadyExistsException extends RuntimeException{

    public IdempotencyKeyAlreadyExistsException() {
        super("Request already processed or in progress");

    }
}
