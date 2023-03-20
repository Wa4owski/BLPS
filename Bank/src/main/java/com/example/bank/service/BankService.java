package com.example.bank.service;

import com.example.bank.exeption.IllegalCardNumberException;
import com.example.bank.exeption.WrongPasswordException;
import com.example.bank.model.TransferRequest;
import com.example.bank.model.TransferResponse;
import com.example.bank.repository.AccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankService {

    @Autowired
    AccountRepo accountRepo;

    public TransferResponse makeTransfer(TransferRequest transferRequest){
        var senderAccount = accountRepo.findByCardNumber(transferRequest.getSenderCardNumber())
                        .orElseThrow(() -> new IllegalCardNumberException("Неверный идентификатор счета отправителя"));
        if(!senderAccount.getPassword().equals(transferRequest.getSenderCardPassword())){
            throw new WrongPasswordException("Предоставелнный пароль не соответсует паролю счета");
        }
        var recipientAccount = accountRepo.findByCardNumber(transferRequest.getSenderCardNumber())
                .orElseThrow(() -> new IllegalCardNumberException("Неверный идентификатор счета получателя"));
        if(transferRequest.getSenderCardNumber().equals(transferRequest.getRecipientCardNumber())){
            throw new IllegalCardNumberException("Номера счетов пользователя и отправителя совпадают");
        }
        if(senderAccount.getBalance() >= transferRequest.getSum()){
            senderAccount.setBalance(senderAccount.getBalance() - transferRequest.getSum());
            recipientAccount.setBalance(recipientAccount.getBalance() + transferRequest.getSum());
            accountRepo.save(senderAccount);
            accountRepo.save(recipientAccount);
            return new TransferResponse(true, "Транзакция успешно завершена");
        }
        return new TransferResponse(false, "На счете отправителя недостаточно средств");
    }
}
