package com.example.bank.exeption;

public class WrongPasswordException extends IllegalArgumentException {
    public WrongPasswordException(String s) {
        super(s);
    }
}
