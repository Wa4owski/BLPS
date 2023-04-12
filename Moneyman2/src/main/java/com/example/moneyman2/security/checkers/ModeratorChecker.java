package com.example.moneyman2.security.checkers;

import com.example.moneyman2.entity.ModeratorEntity;
import com.example.moneyman2.repository.ModeratorRepo;
import com.example.moneyman2.security.UserPrincipal;
import com.example.moneyman2.security.UserRole;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ModeratorChecker extends UserRoleChecker{
    private ModeratorRepo repository;

    public ModeratorChecker(ModeratorRepo repository) {
        this.repository = repository;
    }

    @Override
    public UserPrincipal check(String username) {
        final Optional<ModeratorEntity> moderatorOp = repository.findByLogin(username);
        if(moderatorOp.isPresent()){
            var moderator = moderatorOp.get();
            return UserPrincipal.builder()
                    .userRole(UserRole.ROLE_MODERATOR)
                    .userId(moderator.getId())
                    .username(moderator.getLogin())
                    .password(moderator.getPassword())
                    .build();
        }
        return checkNext(username);
    }
}
