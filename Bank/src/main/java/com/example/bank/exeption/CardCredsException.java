package com.example.bank.exeption;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardCredsException extends IllegalArgumentException{
    Integer loanApplicationId;
    public CardCredsException(Integer loanApplicationId, String message) {
        super(String.format("Заявка %d: %s", loanApplicationId, message));
    }
}
