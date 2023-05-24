package com.example.bank.exeption;

public class IllegalCardNumberException extends CardCredsException{
    public IllegalCardNumberException(Integer loanApplicationId, String message) {
        super(loanApplicationId, message);
        this.loanApplicationId = loanApplicationId;
    }
}
