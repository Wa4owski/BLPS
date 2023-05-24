package com.example.moneyman2.controller;

import com.example.moneyman2.dto.LoanApplicationDTO;
import com.example.moneyman2.security.UserPrincipal;
import com.example.moneyman2.service.ModeratorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/moderator")
public class ModeratorController {

    private final ModeratorService moderatorService;

    public ModeratorController(ModeratorService moderatorService) {
        this.moderatorService = moderatorService;
    }

    @GetMapping("/applications/active")
    public List<LoanApplicationDTO> getActiveLoanApplications(@RequestParam String email,
                                                              Authentication auth) throws IllegalAccessException {
        var userPrincipal = (UserPrincipal) auth.getPrincipal();
        Integer moderatorId = userPrincipal.getUserId();
        return moderatorService.getActiveLoanApplicationsByEmail(moderatorId, email);
    }

    @PostMapping(path = "/application/{id}/approve")
    public void approveLoan(@PathVariable("id") Integer applicationId,
                                         Authentication auth) throws IllegalAccessException, JsonProcessingException {
        var userPrincipal = (UserPrincipal) auth.getPrincipal();
        Integer moderatorId = userPrincipal.getUserId();
        moderatorService.approveLoanRequest(moderatorId, applicationId);
    }

    @PostMapping(path = "/application/{id}/reject")
    public void rejectLoan(@PathVariable("id") Integer applicationId,
                                         @RequestParam("reason") String reason,
                                         Authentication auth) throws IllegalAccessException {
        var userPrincipal = (UserPrincipal) auth.getPrincipal();
        Integer moderatorId = userPrincipal.getUserId();
        moderatorService.rejectLoanRequest(moderatorId, applicationId, reason);
    }

}
