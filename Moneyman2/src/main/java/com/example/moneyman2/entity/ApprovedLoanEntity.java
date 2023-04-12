package com.example.moneyman2.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;


@Entity
@Table(name = "approved_loan")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ApprovedLoanEntity {
    @Id
    private Integer loanApplicationId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "loan_application_id")
    private LoanApplicationEntity loanApplication;

    private Integer sumToReturn;

    private LocalDate returnDate;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "moderator_id")
    private ModeratorEntity moderator;

    @CreationTimestamp
    private Timestamp createdAt;

    public void setLoanApplication(LoanApplicationEntity loanApplication) {
        this.loanApplication = loanApplication;
        loanApplication.setIsActive(false);
    }
}
