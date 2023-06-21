package itmo.blps.repository;


import itmo.blps.entity.ApprovedLoanEntity;
import itmo.blps.model.ApprovedLoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovedLoanRepo extends JpaRepository<ApprovedLoanEntity, Integer> {
    List<ApprovedLoanEntity> findAllByStatus(ApprovedLoanStatus status);
}
