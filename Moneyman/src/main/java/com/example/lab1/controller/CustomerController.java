package com.example.lab1.controller;

import com.example.lab1.dto.*;
import com.example.lab1.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @PostMapping(path = "/chooseLoan")
    public Integer chooseLoan(@Valid @RequestBody LoanDTO loanDTO){
        return customerService.processLoanRequest(loanDTO);
    }

    @GetMapping(path = "/calcLoan")
    public LoanCalcResponse calculateLoan(@Valid @RequestBody LoanDTO loanDTO){
        return customerService.calcLoanRequest(loanDTO);
    }

    @PostMapping(path = "/user/{id}/basicData")
    public void postBasicUserData(@PathVariable(name = "id") Integer userId,
                                  @Valid @RequestBody UserDetailsDTO userDetails) throws IllegalAccessException {
        customerService.postUserDetails(userId, userDetails);
    }

    @PostMapping(path = "/user/{id}/passportData")
    public void postBasicUserData(@PathVariable(name = "id") Integer userId,
                                  @Valid @RequestBody PassportDetailsDTO passportDetails) throws IllegalAccessException {
        customerService.postPassportDetails(userId, passportDetails);
    }

    @PostMapping(path = "/user/{id}/makeTransfer")
    public TransferResponse makeTransfer(@PathVariable("id") Integer userId,
                                         @Valid @RequestBody CardCredentials cardCredentials) throws IllegalAccessException {
        return customerService.makeTransfer(userId, cardCredentials);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(
            IllegalArgumentException ex) {
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(IllegalAccessException.class)
    public String handleIllegalAccess(
            IllegalAccessException ex) {
        return ex.getMessage();
    }

}
