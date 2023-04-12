package com.example.moneyman2.service;


import com.example.moneyman2.dto.LoanApplicationDTO;
import com.example.moneyman2.dto.LoanParamsDTO;
import com.example.moneyman2.dto.TransferRequest;
import com.example.moneyman2.dto.TransferResponse;
import com.example.moneyman2.entity.ApprovedLoanEntity;
import com.example.moneyman2.entity.LoanApplicationEntity;
import com.example.moneyman2.entity.ModeratorEntity;
import com.example.moneyman2.entity.RejectedLoanEntity;
import com.example.moneyman2.repository.LoanApplicationRepo;
import com.example.moneyman2.repository.ModeratorRepo;
import com.example.moneyman2.repository.UserDetailsRepo;
import com.example.moneyman2.retrofit.BankService;
import com.example.moneyman2.retrofit.ServiceGenerator;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.moneyman2.service.CustomerService.calcLoanRequest;

@Service
public class ModeratorService {

    private static final String CARD_NUMBER =  "1234-1234-4321-4321";
    private static final String PASSWORD = "password";

    private final LoanApplicationRepo loanApplicationRepo;
    private final UserDetailsRepo customerRepo;

    private final ModeratorRepo moderatorRepo;

    public ModeratorService(LoanApplicationRepo loanApplicationRepo, UserDetailsRepo customerRepo, ModeratorRepo moderatorRepo) {
        this.loanApplicationRepo = loanApplicationRepo;
        this.customerRepo = customerRepo;
        this.moderatorRepo = moderatorRepo;
    }

    private final BankService bankService = ServiceGenerator.createService(BankService.class);


    public List<LoanApplicationDTO> getActiveLoanApplicationsByEmail(Integer moderatorId, String email) throws IllegalAccessException {
        var moderator = moderatorRepo.findById(moderatorId)
                .orElseThrow(() -> new IllegalAccessException("Модератор с таким id не надйен"));
        var customer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с такой почтой не найден"));
        return customer.getLoanApplications().stream()
                .filter(LoanApplicationEntity::getIsActive)
                .map(LoanApplicationDTO::new)
                .collect(Collectors.toList());
    }

    public TransferResponse approveLoanRequest(Integer moderatorId, Integer applicationId) throws IllegalAccessException {
        var moderator = moderatorRepo.findById(moderatorId)
                .orElseThrow(() -> new IllegalAccessException("Модератор с таким id не надйен"));
        var loanApp = loanApplicationRepo.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Неверный идентификатор заявки"));

        var transferRequest = TransferRequest.builder()
                .sum(loanApp.getSum())
                .recipientCardNumber(loanApp.getCardNumber())
                .senderCardNumber(CARD_NUMBER)
                .senderCardPassword(PASSWORD)
                .build();

        Call<TransferResponse> call = bankService.makeTransfer(transferRequest);
        try {
            Response<TransferResponse> response = call.execute();
            if(response.isSuccessful()) {
                processApprovedLoan(loanApp, moderator);
                return response.body();
            }
            return new Gson().fromJson(response.errorBody().string(),TransferResponse.class);
        }
        catch (RuntimeException ex) {
            return new TransferResponse(false, "Банковский сервис недоступен");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void processApprovedLoan(LoanApplicationEntity loan, ModeratorEntity moderator) {
        var loanCalcResponse = calcLoanRequest(new LoanParamsDTO(loan.getSum(), loan.getTerm()));
        var approvedLoan = ApprovedLoanEntity.builder()
                .sumToReturn(loanCalcResponse.getSumToReturn())
                .returnDate(loanCalcResponse.getReturnDate())
                .build();

        approvedLoan.setLoanApplication(loan);
        loanApplicationRepo.save(loan);
        moderator.addApprovedLoan(approvedLoan);
        moderatorRepo.save(moderator);
    }

    @Transactional(
            isolation = Isolation.READ_COMMITTED,
            timeout = 3,
            rollbackFor = {Exception.class}
    )
    public void rejectLoanRequest(Integer moderatorId, Integer applicationId, String reason) throws IllegalAccessException {
        var moderator = moderatorRepo.findById(moderatorId)
                .orElseThrow(() -> new IllegalAccessException("Модератор с таким id не надйен"));
        var loanApp = loanApplicationRepo.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Неверный идентификатор заявки"));
        if(!loanApp.getIsActive()){
            throw new IllegalArgumentException("Эта заявка уже обработана");
        }
        var rejectedLoan = RejectedLoanEntity.builder()
                .reason(reason)
                .build();

        rejectedLoan.setLoanApplication(loanApp);
        loanApplicationRepo.save(loanApp);
        moderator.addRejectedLoan(rejectedLoan);
        moderatorRepo.save(moderator);
    }
}
