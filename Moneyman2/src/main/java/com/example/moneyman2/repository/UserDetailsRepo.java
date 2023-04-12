package com.example.moneyman2.repository;

import com.example.moneyman2.entity.UserDetailsEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDetailsRepo extends JpaRepository<UserDetailsEntity, Integer> {

    Optional<UserDetailsEntity> findByEmail(String email);

    Optional<UserDetailsEntity> findById(Integer id);
}
