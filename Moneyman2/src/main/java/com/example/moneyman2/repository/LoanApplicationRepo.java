package com.example.moneyman2.repository;

import com.example.moneyman2.entity.LoanApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanApplicationRepo extends JpaRepository<LoanApplicationEntity, Integer> {

}
