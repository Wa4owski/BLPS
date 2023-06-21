package itmo.blps.repository;


import itmo.blps.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepo extends JpaRepository<AccountEntity, String> {
    Optional<AccountEntity> findByCardNumber(String cardNumber);
}
