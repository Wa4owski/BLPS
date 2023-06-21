package itmo.blps.delegate;

import itmo.blps.dto.TransferRequest;
import itmo.blps.entity.ApprovedLoanEntity;
import itmo.blps.model.ApprovedLoanStatus;
import itmo.blps.repository.ApprovedLoanRepo;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Named;

@Component
@Named
public class TransferRequestSendToBankDelegate implements JavaDelegate {
    private final ApprovedLoanRepo approvedLoanRepo;

    private static final String CARD_NUMBER = "1234-1234-4321-4321";
    private static final String PASSWORD = "password";

    public TransferRequestSendToBankDelegate(ApprovedLoanRepo approvedLoanRepo) {
        this.approvedLoanRepo = approvedLoanRepo;
    }

    @Override
    @Transactional
    public void execute(DelegateExecution delegateExecution) throws Exception {
        var retryList = approvedLoanRepo.findAllByStatus(ApprovedLoanStatus.RETRY_NEEDED);

          for (ApprovedLoanEntity approvedLoan : retryList) {
            var transferRequest = TransferRequest.builder()
                    .approvedAppId(approvedLoan.getLoanApplication().getId())
                    .sum(approvedLoan.getLoanApplication().getSum())
                    .recipientCardNumber(approvedLoan.getLoanApplication().getCardNumber())
                    .senderCardNumber(CARD_NUMBER)
                    .senderCardPassword(PASSWORD)
                    .build();
            approvedLoan.setStatus(ApprovedLoanStatus.NOT_PROCESSED);
            approvedLoanRepo.save(approvedLoan);
            delegateExecution
                    .getProcessEngineServices()
                    .getRuntimeService()
                    .createMessageCorrelation("message-to-bank")
                    .setVariable("transfer-request", transferRequest)
                    .correlate();
        }
    }
}
