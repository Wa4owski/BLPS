package itmo.blps.delegate;

import itmo.blps.dto.LoanCalcResponse;
import itmo.blps.dto.LoanParamsDTO;
import itmo.blps.dto.TransferRequest;
import itmo.blps.entity.ApprovedLoanEntity;
import itmo.blps.entity.RejectedLoanEntity;
import itmo.blps.model.ApprovedLoanStatus;
import itmo.blps.repository.ApprovedLoanRepo;
import itmo.blps.repository.LoanApplicationRepo;
import itmo.blps.repository.RejectedLoanRepo;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Named;
import java.time.LocalDate;

@Component
@Named
public class ManageLoanRequestDelegate implements JavaDelegate {
    private static final String CARD_NUMBER = "1234-1234-4321-4321";
    private static final String PASSWORD = "password";
    private final LoanApplicationRepo loanApplicationRepo;

    private final ApprovedLoanRepo approvedLoanRepo;

    private final RejectedLoanRepo rejectedLoanRepo;

    private static final double INTEREST = 0.01;

    public ManageLoanRequestDelegate(LoanApplicationRepo loanApplicationRepo, ApprovedLoanRepo approvedLoanRepo, RejectedLoanRepo rejectedLoanRepo) {
        this.loanApplicationRepo = loanApplicationRepo;
        this.approvedLoanRepo = approvedLoanRepo;
        this.rejectedLoanRepo = rejectedLoanRepo;
    }

    @Override
    public void execute(DelegateExecution delegateExecution)  {
        try {
            int requestId = ((Long) delegateExecution.getVariable("request_id")).intValue();
            boolean verdict = (Boolean) delegateExecution.getVariable("verdict");
            var loanApp = loanApplicationRepo.findById(requestId)
                    .orElseThrow(() -> new IllegalArgumentException("Неверный идентификатор заявки"));
            if (!loanApp.getIsActive()) {
                throw new IllegalArgumentException("Заявка с таким id уже обработана");
            }

            if (verdict) {
                var loanCalcResponse = calcLoanRequest(new LoanParamsDTO(loanApp.getSum(), loanApp.getTerm()));
                var approvedLoan = ApprovedLoanEntity.builder()
                        .sumToReturn(loanCalcResponse.getSumToReturn())
                        .returnDate(loanCalcResponse.getReturnDate())
                        .build();

                approvedLoan.setLoanApplication(loanApp);
                approvedLoan.setStatus(ApprovedLoanStatus.NOT_PROCESSED);
                loanApplicationRepo.save(loanApp);
                approvedLoanRepo.save(approvedLoan);

                var transferRequest = TransferRequest.builder()
                        .approvedAppId(approvedLoan.getLoanApplication().getId())
                        .sum(approvedLoan.getLoanApplication().getSum())
                        .recipientCardNumber(approvedLoan.getLoanApplication().getCardNumber())
                        .senderCardNumber(CARD_NUMBER)
                        .senderCardPassword(PASSWORD)
                        .build();
                approvedLoan.setStatus(ApprovedLoanStatus.NOT_PROCESSED);                delegateExecution
                        .getProcessEngineServices()
                        .getRuntimeService()
                        .createMessageCorrelation("message-to-bank")
                        .setVariable("transfer-request", transferRequest)
                        .correlate();
            } else {
                var rejectedLoan = RejectedLoanEntity.builder()
                        .reason("some reason")
                        .build();

                rejectedLoan.setLoanApplication(loanApp);
                loanApplicationRepo.save(loanApp);
                rejectedLoanRepo.save(rejectedLoan);
            }

        }
        catch (Throwable throwable){
            throw new BpmnError(throwable.getMessage());
        }
    }

    public static LoanCalcResponse calcLoanRequest(LoanParamsDTO loanParamsDTO) {
        LocalDate returnDate = LocalDate.now().plusDays(loanParamsDTO.getTerm());
        Integer sumToReturn = (int) (loanParamsDTO.getSum() * Math.pow((1d + INTEREST), loanParamsDTO.getTerm()*1.0));
        return new LoanCalcResponse(sumToReturn, returnDate);
    }
}
