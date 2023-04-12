package com.example.moneyman2.entity;

import com.example.moneyman2.dto.UserDetailsDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "user_details")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDetailsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String email;

    @Column(name = "passwrd", nullable = false)
    private String password;

    public UserDetailsEntity(UserDetailsDTO userDetailsDTO) {
        this.password = userDetailsDTO.getPassword();
        this.email = userDetailsDTO.getEmail();
    }

    @OneToOne(mappedBy = "userDetails", cascade = CascadeType.ALL)
    private PassportDetailsEntity passportDetails;

    public void setPassportDetails(PassportDetailsEntity passportDetails) {
        this.passportDetails = passportDetails;
        passportDetails.setUserDetails(this);
    }

    @Setter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "userDetails", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<LoanApplicationEntity> loanApplications;

    public void addLoanApplicationEntity(LoanApplicationEntity loanApp) {
        this.loanApplications.add(loanApp);
        loanApp.setUserDetails(this);
    }

}
