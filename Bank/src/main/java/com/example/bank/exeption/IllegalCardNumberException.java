package com.example.bank.exeption;

public class IllegalCardNumberException extends IllegalArgumentException{
    public IllegalCardNumberException(String s) {
        super(s);
    }
}
