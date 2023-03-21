package com.example.lab1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;


@Entity
@Table(name = "approved_loan")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ApprovedLoanEntity {
    @Id
    private Integer customerId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private LoanEntity loanEntity;

    private Integer sumToReturn;

    private LocalDate returnDate;

    @CreationTimestamp
    private Timestamp createdAt;
}
