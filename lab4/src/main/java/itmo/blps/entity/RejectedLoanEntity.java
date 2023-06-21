package itmo.blps.entity;

import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.sql.Timestamp;
import java.time.LocalDate;


@Entity
@Table(name = "rejected_loan")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RejectedLoanEntity {
    @Id
    private Integer loanApplicationId;

    @MapsId
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "loan_application_id")
    private LoanApplicationEntity loanApplication;

    private String reason;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "moderator_id")
    private ModeratorEntity moderator;

    @CreationTimestamp
    private Timestamp createdAt;

    public void setLoanApplication(LoanApplicationEntity loanApplication) {
        this.loanApplication = loanApplication;
        //this.loanApplicationId = loanApplication.getId();
        loanApplication.setIsActive(false);
    }
}
