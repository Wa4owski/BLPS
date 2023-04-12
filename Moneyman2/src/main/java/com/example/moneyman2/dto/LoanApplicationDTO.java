package com.example.moneyman2.dto;

import com.example.moneyman2.entity.LoanApplicationEntity;
import lombok.Data;

@Data
public class LoanApplicationDTO {

    private Integer id;
    private LoanParamsDTO loanParamsDTO;
    private String cardNumber;

    public LoanApplicationDTO(LoanApplicationEntity loanApplication) {
        this.loanParamsDTO = new LoanParamsDTO(loanApplication.getSum(), loanApplication.getTerm());
        this.cardNumber = loanApplication.getCardNumber();
        this.id = loanApplication.getId();
    }
}
