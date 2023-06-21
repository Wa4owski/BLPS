package itmo.blps.repository;

import itmo.blps.entity.LoanApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanApplicationRepo extends JpaRepository<LoanApplicationEntity, Integer> {

}
