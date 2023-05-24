package com.example.moneyman2.repository;


import com.example.moneyman2.entity.ApprovedLoanEntity;
import com.example.moneyman2.model.ApprovedLoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovedLoanRepo extends JpaRepository<ApprovedLoanEntity, Integer> {
    List<ApprovedLoanEntity> findAllByStatus(ApprovedLoanStatus status);
}
