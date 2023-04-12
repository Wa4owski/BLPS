package com.example.moneyman2.entity;

import com.example.moneyman2.dto.LoanApplicationRequest;
import com.example.moneyman2.dto.LoanParamsDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "loan", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoanApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private UserDetailsEntity userDetails;

    private Integer sum;

    private Integer term;

    private String cardNumber;

    private Boolean isActive;

    @CreationTimestamp
    private Timestamp arrangedAt;

    public LoanApplicationEntity(LoanApplicationRequest loanApplicationRequest) {
        this.sum = loanApplicationRequest.getLoanParamsDTO().getSum();
        this.term = loanApplicationRequest.getLoanParamsDTO().getTerm();
        this.cardNumber = loanApplicationRequest.getCardCredentials().getCardNumber();
        this.isActive = true;
    }
}
