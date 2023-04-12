package com.example.moneyman2.controller;

import com.example.moneyman2.dto.*;
import com.example.moneyman2.security.UserPrincipal;
import com.example.moneyman2.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(path = "/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @PostMapping(path = "/choose-loan")
    public void sentLoanRequest(@Valid @RequestBody LoanApplicationRequest loanApplicationRequest,
                                Authentication auth) throws IllegalAccessException {
        var userPrincipal =  (UserPrincipal) auth.getPrincipal();
        Integer customerId = userPrincipal.getUserId();
        customerService.processLoanRequest(customerId, loanApplicationRequest);
    }

    @GetMapping(path = "/calc-loan")
    public LoanCalcResponse calculateLoan(@Valid @RequestBody LoanParamsDTO loanParamsDTO){
        return customerService.calcLoanRequest(loanParamsDTO);
    }

    @PostMapping(path = "/register")
    public Integer register(@Valid @RequestBody UserDetailsDTO userDetails) throws IllegalAccessException {
        return customerService.postUserDetails(userDetails);
    }

    @PostMapping(path = "/passport-data")
    public void postPassportUserData(@Valid @RequestBody PassportDetailsDTO passportDetails,
                                     Authentication auth) throws IllegalAccessException {
        var userPrincipal =  (UserPrincipal) auth.getPrincipal();
        Integer customerId = userPrincipal.getUserId();
        customerService.postPassportDetails(customerId, passportDetails);
    }


}
