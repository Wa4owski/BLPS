package itmo.blps.delegate;

import itmo.blps.dto.CardCredentials;
import itmo.blps.dto.LoanApplicationRequest;
import itmo.blps.dto.LoanParamsDTO;
import itmo.blps.entity.LoanApplicationEntity;
import itmo.blps.repository.UserDetailsRepo;
import itmo.blps.security.UserPrincipal;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Named;
import java.util.List;

@Component
@Named
public class SaveLoanRequestDelegate implements JavaDelegate {

    private static final Integer MAX_ACTIVE_APPLICATIONS_AMOUNT = 3;

    private final UserDetailsRepo userDetailsRepo;

    public SaveLoanRequestDelegate(UserDetailsRepo userDetailsRepo) {
        this.userDetailsRepo = userDetailsRepo;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        int sum = ((Long) delegateExecution.getVariable("sum") ).intValue();
        int term  = ((Long) delegateExecution.getVariable("term") ).intValue();
        String cardNumber = (String) delegateExecution.getVariable("cardNumber");

        UserPrincipal userPrincipal = (UserPrincipal) delegateExecution.getVariable("user");
        var loanParams = LoanParamsDTO.builder().sum(sum)
                .term(term)
                .build();
        var cardCreds = CardCredentials.builder()
                .cardNumber(cardNumber)
                .cvs("456")
                .expDate("23-01")
                .build();
        var loanApplicationRequest = new LoanApplicationRequest(loanParams, cardCreds);
        var user = userDetailsRepo.findById(userPrincipal.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Неверный идентификатор пользователя"));
//        if(user.getPassportDetails() == null){
//            throw new IllegalAccessException("Отсутсвуют паспортные данные для этого пользователя");
//        }
        if(!canAddAnotherApplication(user.getLoanApplications())){
            throw new IllegalAccessException("Превышено максимально возможное число активных заявок");
        }
        var newLoanApp = new LoanApplicationEntity(loanApplicationRequest);
        user.addLoanApplicationEntity(newLoanApp);
        userDetailsRepo.save(user);
    }

    private boolean canAddAnotherApplication(List<LoanApplicationEntity> apps){
        return apps.stream().filter(LoanApplicationEntity::getIsActive).count() < MAX_ACTIVE_APPLICATIONS_AMOUNT;
    }
}
