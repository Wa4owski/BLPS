package com.example.bank.service;

import com.example.bank.exeption.CardCredsException;
import com.example.bank.exeption.IllegalCardNumberException;
import com.example.bank.exeption.WrongPasswordException;
import com.example.bank.model.CardCreds;
import com.example.bank.model.TransferRequest;
import com.example.bank.model.TransferResponse;
import com.example.bank.model.TransferStatus;
import com.example.bank.repository.AccountRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.jms.client.RMQMessage;
import com.rabbitmq.jms.client.message.RMQBytesMessage;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
public class BankService {

    private final AccountRepo accountRepo;

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BankService(AccountRepo accountRepo, RabbitTemplate rabbitTemplate) {
        this.accountRepo = accountRepo;
        this.rabbitTemplate = rabbitTemplate;
    }

    @JmsListener(destination = "mm-to-bank")
    @Transactional
    public void receiveTransferRequest(Message message) throws JMSException, IOException {
        byte[] bytes = message.getBody(byte[].class);
        TransferRequest transferRequest = objectMapper.readValue(bytes, TransferRequest.class);
        TransferResponse response;
        try {
            response = makeTransfer(transferRequest);
        }
        catch (CardCredsException ex) {
            response = TransferResponse.builder()
                    .approvedAppId(ex.getLoanApplicationId())
                    .code(TransferStatus.INVALID_CARD_CREDS.ordinal())
                    .comment(ex.getMessage())
                    .build();
        }
        rabbitTemplate.convertAndSend("bank-to-mm", objectMapper.writeValueAsString(response));
    }

    /*
    @JmsListener(destination = "myQueue")
    public void receiveTransferRequest(RMQMessage message) throws JMSException, IOException {
        System.out.println(message.getJMSType());
        byte[] bytes = message.getBody(byte[].class);
        CardCredentials cardCreds = objectMapper.readValue(bytes, CardCredentials.class);
        System.out.println(cardCreds);
    }
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public TransferResponse makeTransfer(TransferRequest transferRequest) throws CardCredsException {
        var senderAccount = accountRepo.findByCardNumber(transferRequest.getSenderCardNumber())
                        .orElseThrow(() -> new IllegalCardNumberException(transferRequest.getApprovedAppId(), "Неверный идентификатор счета отправителя"));
        if(!senderAccount.getPassword().equals(transferRequest.getSenderCardPassword())){
            throw new WrongPasswordException(transferRequest.getApprovedAppId(), "Предоставленный пароль не соответствует паролю счета");
        }
        var recipientAccount = accountRepo.findByCardNumber(transferRequest.getRecipientCardNumber())
                .orElseThrow(() -> new IllegalCardNumberException(transferRequest.getApprovedAppId(), "Неверный идентификатор счета получателя"));
        if(transferRequest.getSenderCardNumber().equals(transferRequest.getRecipientCardNumber())){
            throw new IllegalCardNumberException(transferRequest.getApprovedAppId(), "Номера счетов пользователя и отправителя совпадают");
        }
        if(senderAccount.getBalance() >= transferRequest.getSum()){
            int senderBalance = senderAccount.getBalance() - transferRequest.getSum();
            senderAccount.setBalance(senderBalance);
            recipientAccount.setBalance(recipientAccount.getBalance() + transferRequest.getSum());
            accountRepo.save(senderAccount);
            accountRepo.save(recipientAccount);
            return TransferResponse.builder().approvedAppId(transferRequest.getApprovedAppId())
                    .code(TransferStatus.EXECUTED.ordinal()).comment("Транзакция успешно завершена").build();
        }
        return TransferResponse.builder().approvedAppId(transferRequest.getApprovedAppId())
                .code(TransferStatus.RETRY_NEEDED.ordinal()).comment("На счете отправителя недостаточно средств").build();
    }

    public Integer checkBalance(CardCreds cardCreds) {
        var account = accountRepo.findByCardNumber(cardCreds.getNumber())
                .orElseThrow(() -> new IllegalCardNumberException(null, "Неверный идентификатор счета"));
        if(!account.getPassword().equals(cardCreds.getPassword())){
            throw new WrongPasswordException(null, "Предоставленный пароль не соответствует паролю счета");
        }
        return account.getBalance();
    }
}
