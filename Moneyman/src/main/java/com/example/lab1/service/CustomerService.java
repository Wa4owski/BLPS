package com.example.lab1.service;

import com.example.lab1.dto.*;
import com.example.lab1.entity.ApprovedLoanEntity;
import com.example.lab1.entity.LoanEntity;
import com.example.lab1.entity.PassportDetailsEntity;
import com.example.lab1.entity.UserDetailsEntity;
import com.example.lab1.repository.LoanRepo;
import com.example.lab1.retrofit.BankService;
import com.example.lab1.retrofit.ServiceGenerator;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    private void processLoan(LoanEntity loan){
        var loanCalcResponse = calcLoanRequest(new LoanDTO(loan.getSum(), loan.getTerm()));
        var approvedLoan = new ApprovedLoanEntity();
        approvedLoan.setLoanEntity(loan);
        approvedLoan.setSumToReturn(loanCalcResponse.getSumToReturn());
        approvedLoan.setReturnDate(loanCalcResponse.getReturnDate());
        loan.setApprovedLoan(approvedLoan);
        loanRepo.save(loan);
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

    @Transactional
    public TransferResponse makeTransfer(Integer userId, CardCredentials cardCredentials) throws IllegalAccessException {
        var loan = loanRepo.findByCustomerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Неверный идентификатор пользователя"));
        if(loan.getUserDetails() == null){
            throw new IllegalAccessException("Данные с предыдущего этапа отсутствуют");
        }
        if(loan.getPassportDetails() == null){
            throw new IllegalAccessException("Данные с предыдущего этапа отсутствуют");
        }
        var transferRequest = new TransferRequest();
        transferRequest.setRecipientCardNumber(cardCredentials.getCardNumber());
        transferRequest.setSum(loan.getSum());
        transferRequest.setSenderCardNumber(CARD_NUMBER);
        transferRequest.setSenderCardPassword(PASSWORD);
        Call<TransferResponse> call = bankService.makeTransfer(transferRequest);
        try {
            Response<TransferResponse> response = call.execute();
            if(response.isSuccessful()) {
                processLoan(loan);
                return response.body();
            }
            return new Gson().fromJson(response.errorBody().string(),TransferResponse.class);
        }
        catch (Exception ex) {
            return new TransferResponse(false, "Банковский сервис недоступен");
        }
    }
}
