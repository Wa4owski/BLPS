package com.example.moneyman2.exception;

public class BankResponseException extends RuntimeException{
    public BankResponseException(String message) {
        super(message);
    }
}
