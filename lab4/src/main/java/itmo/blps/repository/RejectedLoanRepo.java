package itmo.blps.repository;

import itmo.blps.entity.RejectedLoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RejectedLoanRepo extends JpaRepository<RejectedLoanEntity, Integer> {
}
