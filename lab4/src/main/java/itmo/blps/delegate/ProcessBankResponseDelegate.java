package itmo.blps.delegate;

import itmo.blps.dto.TransferResponse;
import itmo.blps.exception.BankResponseException;
import itmo.blps.model.ApprovedLoanStatus;
import itmo.blps.repository.ApprovedLoanRepo;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Named;

@Component
@Named
public class ProcessBankResponseDelegate implements JavaDelegate {

    private final ApprovedLoanRepo approvedLoanRepo;

    public ProcessBankResponseDelegate(ApprovedLoanRepo approvedLoanRepo) {
        this.approvedLoanRepo = approvedLoanRepo;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        TransferResponse transferResponse = (TransferResponse) delegateExecution.getVariable("transfer-response");
        var approvedLoan = approvedLoanRepo.findById(transferResponse.getApprovedAppId())
                .orElseThrow(() -> new BankResponseException("Нет одобренной заявки с таким id"));
        if(approvedLoan.getStatus().equals(ApprovedLoanStatus.EXECUTED)){
            throw new BankResponseException("Этот платеж уже был исполнен");
        }
        ApprovedLoanStatus status = ApprovedLoanStatus.values()[transferResponse.getCode()];
        approvedLoan.setStatus(status);
        approvedLoanRepo.save(approvedLoan);
    }
}
