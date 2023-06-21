package itmo.blps.entity;

import javax.persistence.*;

import itmo.blps.model.ApprovedLoanStatus;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "loan_application_id")
    private LoanApplicationEntity loanApplication;

    private Integer sumToReturn;

    private LocalDate returnDate;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "moderator_id")
    private ModeratorEntity moderator;

    @Enumerated(EnumType.STRING)
    private ApprovedLoanStatus status;

    @CreationTimestamp
    private Timestamp createdAt;

    public void setLoanApplication(LoanApplicationEntity loanApplication) {
        this.loanApplication = loanApplication;
        loanApplication.setIsActive(false);
    }
}
