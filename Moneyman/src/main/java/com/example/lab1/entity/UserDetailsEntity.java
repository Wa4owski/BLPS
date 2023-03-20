package com.example.lab1.entity;

import com.example.lab1.dto.UserDetailsDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_details")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDetailsEntity {
    @Id
    private Integer customerId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private LoanEntity loanEntity;

    @Column(name = "full_name", nullable = false)
    String fullName;

    @Column(nullable = false)
    String email;

    public UserDetailsEntity(UserDetailsDTO userDetailsDTO) {
        this.fullName = userDetailsDTO.getFullName();
        this.email = userDetailsDTO.getEmail();
    }

}
