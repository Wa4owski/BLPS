package itmo.blps.security.checkers;

import itmo.blps.entity.UserDetailsEntity;
import itmo.blps.repository.UserDetailsRepo;
import itmo.blps.security.UserPrincipal;
import itmo.blps.security.UserRole;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomerChecker extends UserRoleChecker{
    private final UserDetailsRepo repository;

    public CustomerChecker(UserDetailsRepo repository) {
        this.repository = repository;
    }
    @Override
    public UserPrincipal check(String username) {
        final Optional<UserDetailsEntity> customerOp = repository.findByEmail(username);
        if(customerOp.isPresent()){
            var customer = customerOp.get();
            return UserPrincipal.builder()
                    .userRole(UserRole.ROLE_CUSTOMER)
                    .userId(customer.getId())
                    .username(customer.getEmail())
                    .password(customer.getPassword())
                    .build();
        }
        return checkNext(username);
    }
}
