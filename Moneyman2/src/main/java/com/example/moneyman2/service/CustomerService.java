package com.example.moneyman2.service;


import com.example.moneyman2.dto.*;
import com.example.moneyman2.entity.ApprovedLoanEntity;
import com.example.moneyman2.entity.LoanApplicationEntity;
import com.example.moneyman2.entity.PassportDetailsEntity;
import com.example.moneyman2.entity.UserDetailsEntity;
import com.example.moneyman2.repository.UserDetailsRepo;
import com.example.moneyman2.retrofit.BankService;
import com.example.moneyman2.retrofit.ServiceGenerator;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;


import java.time.LocalDate;
import java.util.List;

@Service
public class CustomerService {

    private static final double INTEREST = 0.01;

    private static final Integer MAX_ACTIVE_APPLICATIONS_AMOUNT = 3;

    private UserDetailsRepo userDetailsRepo;

    public CustomerService(UserDetailsRepo userDetailsRepo) {
        this.userDetailsRepo = userDetailsRepo;
    }

    public static LoanCalcResponse calcLoanRequest(LoanParamsDTO loanParamsDTO) {
        LocalDate returnDate = LocalDate.now().plusDays(loanParamsDTO.getTerm());
        Integer sumToReturn = (int) (loanParamsDTO.getSum() * Math.pow((1d + INTEREST), loanParamsDTO.getTerm()*1.0));
        return new LoanCalcResponse(sumToReturn, returnDate);
    }


    public Integer postUserDetails(UserDetailsDTO userDetails) throws IllegalAccessException {
        var userOpt = userDetailsRepo.findByEmail(userDetails.getEmail());
        if(userOpt.isPresent()){
            throw new IllegalAccessException("Пользователь с такой почтой уже зарегистрирован");
        }
        var newUser = new UserDetailsEntity(userDetails);
        return userDetailsRepo.save(newUser).getId();
    }

    public void postPassportDetails(Integer userId, PassportDetailsDTO passportDetails) throws IllegalAccessException {
        var user = userDetailsRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Неверный идентификатор пользователя"));
        if(user.getPassportDetails() != null){
            throw new IllegalAccessException("паспортные данные для этого пользователя уже введены");
        }
        user.setPassportDetails((new PassportDetailsEntity(passportDetails)));
        userDetailsRepo.save(user);
    }

    public void processLoanRequest(Integer userId, LoanApplicationRequest loanApplicationRequest) throws IllegalAccessException {
        var user = userDetailsRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Неверный идентификатор пользователя"));
        if(user.getPassportDetails() == null){
            throw new IllegalAccessException("Отсутсвуют паспортные данные для этого пользователя");
        }
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
