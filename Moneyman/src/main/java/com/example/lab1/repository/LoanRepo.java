package com.example.lab1.repository;

import com.example.lab1.entity.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanRepo extends JpaRepository<LoanEntity, Integer> {

    Optional<LoanEntity> findByCustomerId(Integer userId);
}
