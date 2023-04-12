package com.example.moneyman2.security;

import com.example.moneyman2.security.checkers.CustomerChecker;
import com.example.moneyman2.security.checkers.ModeratorChecker;
import com.example.moneyman2.security.checkers.UserRoleChecker;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final CustomerChecker customerChecker;
    private final ModeratorChecker moderatorChecker;

    public MyUserDetailsService(CustomerChecker customerChecker, ModeratorChecker moderatorChecker) {
        this.customerChecker = customerChecker;
        this.moderatorChecker = moderatorChecker;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserRoleChecker userRoleChecker = UserRoleChecker.link(
                customerChecker,
                moderatorChecker
        );
        UserPrincipal userPrincipal = userRoleChecker.check(username);
        if(userPrincipal == null){
            throw new UsernameNotFoundException("Такого аккаунта не существует");
        }
        return userPrincipal;
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
