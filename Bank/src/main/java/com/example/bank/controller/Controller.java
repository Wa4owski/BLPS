package com.example.bank.controller;

import com.example.bank.exeption.CardCredsException;
import com.example.bank.model.CardCreds;
import com.example.bank.model.TransferRequest;
import com.example.bank.model.TransferResponse;
import com.example.bank.model.TransferStatus;
import com.example.bank.service.BankService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class Controller {

    @Autowired
    BankService bankService;

    @PostMapping("/checkBalance")
    public Integer checkBalance(@Valid @RequestBody CardCreds cardCreds){
        return bankService.checkBalance(cardCreds);
    }

    @PostMapping("/makeTransfer")
    public TransferResponse makeTransfer(@Valid @RequestBody TransferRequest transferRequest){
        return bankService.makeTransfer(transferRequest);
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


}
