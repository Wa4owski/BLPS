package itmo.blps.delegate;

import itmo.blps.dto.TransferRequest;
import itmo.blps.dto.TransferResponse;
import itmo.blps.exception.CardCredsException;
import itmo.blps.exception.IllegalCardNumberException;
import itmo.blps.exception.WrongPasswordException;
import itmo.blps.model.TransferStatus;
import itmo.blps.repository.AccountRepo;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Named;

@Component
@Named
public class MakeTransferDelegate implements JavaDelegate {

    private final AccountRepo accountRepo;

    public MakeTransferDelegate(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    @Override
    @Transactional
    public void execute(DelegateExecution delegateExecution) throws Exception {
        TransferRequest transferRequest = (TransferRequest) delegateExecution.getVariable("transfer-request");
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
        delegateExecution
                .getProcessEngineServices()
                .getRuntimeService()
                .createMessageCorrelation("message-to-mm")
                .setVariable("transfer-response", response)
                .correlate();
    }

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

}
