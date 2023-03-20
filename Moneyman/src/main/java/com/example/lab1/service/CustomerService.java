package com.example.lab1.service;

import com.example.lab1.dto.*;
import com.example.lab1.entity.LoanEntity;
import com.example.lab1.entity.PassportDetailsEntity;
import com.example.lab1.entity.UserDetailsEntity;
import com.example.lab1.repository.LoanRepo;
import com.example.lab1.retrofit.BankService;
import com.example.lab1.retrofit.ServiceGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.time.LocalDate;

@Service
public class CustomerService {

    private static final double INTEREST = 0.01;

    private static final String CARD_NUMBER =  "1234-1234-4321-4321";
    private static final String PASSWORD = "password";
    @Autowired
    LoanRepo loanRepo;

    BankService bankService = ServiceGenerator.createService(BankService.class);
    public Integer processLoanRequest(LoanDTO loanDTO){
        LoanEntity newLoan = new LoanEntity(loanDTO);
        return loanRepo.save(newLoan).getCustomerId();
    }

    public LoanCalcResponse calcLoanRequest(LoanDTO loanDTO) {
        LocalDate returnDate = LocalDate.now().plusDays(loanDTO.getTerm());
        Integer sumToReturn = (int) (loanDTO.getSum() * Math.pow((1d + INTEREST), loanDTO.getTerm()*1.0));
        return new LoanCalcResponse(sumToReturn, returnDate);
    }

    public void postUserDetails(Integer userId, UserDetailsDTO userDetails) throws IllegalAccessException {
        var loan = loanRepo.findByCustomerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Неверный идентификатор пользователя"));
        if(loan.getUserDetails() != null){
            throw new IllegalAccessException("Данные для этого пользователя уже введены");
        }
        loan.setUserDetails(new UserDetailsEntity(userDetails));
        loanRepo.save(loan);
    }

    public void postPassportDetails(Integer userId, PassportDetailsDTO passportDetails) throws IllegalAccessException {
        var loan = loanRepo.findByCustomerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Неверный идентификатор пользователя"));
        if(loan.getUserDetails() == null){
            throw new IllegalAccessException("Данные с предыдущего этапа отсутствуют");
        }
        if(loan.getPassportDetails() != null){
            throw new IllegalAccessException("Данные для этого пользователя уже введены");
        }
        loan.setPassportDetails((new PassportDetailsEntity(passportDetails)));
        loanRepo.save(loan);
    }

    public Integer checkBalance(String cardNumber){
        Call<Integer> call = bankService.getBalance(cardNumber);
        try {
            Response<Integer> response = call.execute();
            Integer balance = response.body();
            return balance;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return -1;
    }

    public TransferResponse makeTransfer(Integer userId, CardDetails cardDetails) throws IllegalAccessException {
        var loan = loanRepo.findByCustomerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Неверный идентификатор пользователя"));
        if(loan.getUserDetails() == null){
            throw new IllegalAccessException("Данные с предыдущего этапа отсутствуют");
        }
        if(loan.getPassportDetails() == null){
            throw new IllegalAccessException("Данные с предыдущего этапа отсутствуют");
        }
        var transferRequest = new TransferRequest();
        transferRequest.setRecipientCardNumber(cardDetails.getCardNumber());
        transferRequest.setSum(loan.getSum());
        transferRequest.setSenderCardNumber(CARD_NUMBER);
        transferRequest.setSenderCardPassword(PASSWORD);
        Call<TransferResponse> call = bankService.makeTransfer(transferRequest);
        try {
            Response<TransferResponse> response = call.execute();
            if(response.body() != null) {
                return response.body();
            }
        } finally {
            return new TransferResponse(false, "Банковский сервис недоступен");
        }
    }
}
