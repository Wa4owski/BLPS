package com.example.lab1.entity;

import com.example.lab1.dto.LoanDTO;
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
public class LoanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customerId;

    private Integer sum;

    private Integer term;

    @CreationTimestamp
    private Timestamp arrangedAt;

    public LoanEntity(LoanDTO loanDTO) {
        this.sum = loanDTO.getSum();
        this.term = loanDTO.getTerm();
    }

    @OneToOne(mappedBy = "loanEntity", cascade = CascadeType.ALL)
    private UserDetailsEntity userDetails;

    public void setUserDetails(UserDetailsEntity userDetails) {
        this.userDetails = userDetails;
        userDetails.setLoanEntity(this);
    }

    @OneToOne(mappedBy = "loanEntity", cascade = CascadeType.ALL)
    private PassportDetailsEntity passportDetails;

    public void setPassportDetails(PassportDetailsEntity passportDetails) {
        this.passportDetails = passportDetails;
        passportDetails.setLoanEntity(this);
    }

    @OneToOne(mappedBy = "loanEntity", cascade = CascadeType.ALL)
    private ApprovedLoanEntity approvedLoan;

    public void setApprovedLoan(ApprovedLoanEntity approvedLoan) {
        this.approvedLoan = approvedLoan;
        approvedLoan.setLoanEntity(this);
    }
}
