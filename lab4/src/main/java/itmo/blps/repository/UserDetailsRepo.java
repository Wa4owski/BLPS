package itmo.blps.repository;

import itmo.blps.entity.UserDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDetailsRepo extends JpaRepository<UserDetailsEntity, Integer> {

    Optional<UserDetailsEntity> findByEmail(String email);

    Optional<UserDetailsEntity> findById(Integer id);
}
