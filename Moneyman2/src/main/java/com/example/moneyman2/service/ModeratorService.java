package com.example.moneyman2.service;


import com.example.moneyman2.dto.LoanApplicationDTO;
import com.example.moneyman2.dto.LoanParamsDTO;
import com.example.moneyman2.dto.TransferRequest;
import com.example.moneyman2.dto.TransferResponse;
import com.example.moneyman2.entity.ApprovedLoanEntity;
import com.example.moneyman2.entity.LoanApplicationEntity;
import com.example.moneyman2.entity.RejectedLoanEntity;
import com.example.moneyman2.exception.BankResponseException;
import com.example.moneyman2.model.ApprovedLoanStatus;
import com.example.moneyman2.repository.ApprovedLoanRepo;
import com.example.moneyman2.repository.LoanApplicationRepo;
import com.example.moneyman2.repository.ModeratorRepo;
import com.example.moneyman2.repository.UserDetailsRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.jms.client.RMQMessage;
import jakarta.jms.JMSException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.moneyman2.service.CustomerService.calcLoanRequest;

@Service
public class ModeratorService {

    private static final String CARD_NUMBER =  "1234-1234-4321-4321";
    private static final String PASSWORD = "password";

    private final RabbitTemplate rabbitTemplate;
    private final LoanApplicationRepo loanApplicationRepo;
    private final UserDetailsRepo customerRepo;
    private final ModeratorRepo moderatorRepo;

    private final ApprovedLoanRepo approvedLoanRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ModeratorService(RabbitTemplate rabbitTemplate, LoanApplicationRepo loanApplicationRepo, UserDetailsRepo customerRepo, ModeratorRepo moderatorRepo, ApprovedLoanRepo approvedLoanRepo) {
        this.rabbitTemplate = rabbitTemplate;
        this.loanApplicationRepo = loanApplicationRepo;
        this.customerRepo = customerRepo;
        this.moderatorRepo = moderatorRepo;
        this.approvedLoanRepo = approvedLoanRepo;
    }

    public List<LoanApplicationDTO> getActiveLoanApplicationsByEmail(Integer moderatorId, String email) throws IllegalAccessException {
        moderatorRepo.findById(moderatorId)
                .orElseThrow(() -> new IllegalAccessException("Модератор с таким id не найден"));
        var customer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с такой почтой не найден"));
        return customer.getLoanApplications().stream()
                .filter(LoanApplicationEntity::getIsActive)
                .map(LoanApplicationDTO::new)
                .collect(Collectors.toList());
    }
    @Scheduled(fixedDelay = 10_000)
    @Transactional
    public void sendFailedTransactions(){
        var retryList = approvedLoanRepo.findAllByStatus(ApprovedLoanStatus.RETRY_NEEDED);

        for(ApprovedLoanEntity approvedLoan : retryList){
            var transferRequest = TransferRequest.builder()
                    .approvedAppId(approvedLoan.getLoanApplication().getId())
                    .sum(approvedLoan.getLoanApplication().getSum())
                    .recipientCardNumber(approvedLoan.getLoanApplication().getCardNumber())
                    .senderCardNumber(CARD_NUMBER)
                    .senderCardPassword(PASSWORD)
                    .build();
            approvedLoan.setStatus(ApprovedLoanStatus.NOT_PROCESSED);
            approvedLoanRepo.save(approvedLoan);
            try {
                sendTransferRequest(transferRequest);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Transactional(
            isolation = Isolation.REPEATABLE_READ,
            rollbackFor = {Exception.class}
    )
    public void approveLoanRequest(Integer moderatorId, Integer applicationId) throws IllegalAccessException, JsonProcessingException {
        var moderator = moderatorRepo.findById(moderatorId)
                .orElseThrow(() -> new IllegalAccessException("Модератор с таким id не найден"));
        var loanApp = loanApplicationRepo.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Неверный идентификатор заявки"));
        if(!loanApp.getIsActive()){
            throw new IllegalArgumentException("Заявка с таким id уже обработана");
        }
        var loanCalcResponse = calcLoanRequest(new LoanParamsDTO(loanApp.getSum(), loanApp.getTerm()));
        var approvedLoan = ApprovedLoanEntity.builder()
                .sumToReturn(loanCalcResponse.getSumToReturn())
                .returnDate(loanCalcResponse.getReturnDate())
                .build();

        approvedLoan.setLoanApplication(loanApp);
        approvedLoan.setStatus(ApprovedLoanStatus.NOT_PROCESSED);
        loanApplicationRepo.save(loanApp);
        moderator.addApprovedLoan(approvedLoan);
        moderatorRepo.save(moderator);

        var transferRequest = TransferRequest.builder()
                .approvedAppId(loanApp.getId())
                .sum(loanApp.getSum())
                .recipientCardNumber(loanApp.getCardNumber())
                .senderCardNumber(CARD_NUMBER)
                .senderCardPassword(PASSWORD)
                .build();

        sendTransferRequest(transferRequest);
    }


    @Transactional
    public void sendTransferRequest(TransferRequest transferRequest) throws JsonProcessingException {
        rabbitTemplate.convertAndSend("mm-to-bank", objectMapper.writeValueAsString(transferRequest));
    }

    @JmsListener(destination = "bank-to-mm")
    @Transactional
    public void processBankResponse(RMQMessage message) throws IOException, JMSException {
        byte[] bytes = message.getBody(byte[].class);
        TransferResponse transferResponse = objectMapper.readValue(bytes, TransferResponse.class);
        var approvedLoan = approvedLoanRepo.findById(transferResponse.getApprovedAppId())
                .orElseThrow(() -> new BankResponseException("Нет одобренной заявки с таким id"));
        if(approvedLoan.getStatus().equals(ApprovedLoanStatus.EXECUTED)){
            throw new BankResponseException("Этот плаеж уже был исполнен");
        }
        ApprovedLoanStatus status = ApprovedLoanStatus.values()[transferResponse.getCode()];
        approvedLoan.setStatus(status);
        approvedLoanRepo.save(approvedLoan);
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
