package com.example.moneyman2.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "moderator")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ModeratorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String login;

    @Column(name = "passwrd", nullable = false)
    private String password;

    @Setter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "moderator",  cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<ApprovedLoanEntity> approvedLoans;
    public void addApprovedLoan(ApprovedLoanEntity approvedLoan){
        approvedLoans.add(approvedLoan);
        approvedLoan.setModerator(this);
    }

    @Setter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "moderator",  cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<RejectedLoanEntity> rejectedLoans;
    public void addRejectedLoan(RejectedLoanEntity rejectedLoan){
        rejectedLoans.add(rejectedLoan);
        rejectedLoan.setModerator(this);
    }



}
