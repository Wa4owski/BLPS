package com.example.bank.exeption;

public class WrongPasswordException extends CardCredsException {
    public WrongPasswordException(Integer loanApplicationId, String message) {
        super(loanApplicationId, message);
        this.loanApplicationId = loanApplicationId;
    }
}
