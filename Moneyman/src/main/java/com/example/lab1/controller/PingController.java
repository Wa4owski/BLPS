package com.example.lab1.controller;

import com.example.lab1.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @Autowired
    CustomerService customerService;
    @GetMapping(path = "/ping")
    public Integer ping(){
        return customerService.checkBalance("rnddkenk");
    }
}
