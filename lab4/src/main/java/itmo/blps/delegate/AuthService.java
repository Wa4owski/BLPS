package itmo.blps.delegate;

import itmo.blps.security.MyUserDetailsService;
import itmo.blps.security.UserRole;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.inject.Named;

@Component
@Named
public class AuthService implements JavaDelegate {

    private final MyUserDetailsService myUserDetailsService;

    public AuthService(MyUserDetailsService myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
    }

    @Override
    public void execute(DelegateExecution delegateExecution){
        try {
            String instanceId = delegateExecution.getActivityInstanceId();
            String actName = delegateExecution.getCurrentActivityName();

            String username = (String) delegateExecution.getVariable("email");
            String password = (String) delegateExecution.getVariable("password");

            UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);
            if(!password.equals(userDetails.getPassword())){
                throw new IllegalAccessException("Illegal password");
            }
            if(!actName.contains("пользователя") && !actName.contains("модератора")){
                System.out.println("неверно названа активити!");
                throw new IllegalStateException();
            }
            if(actName.contains("пользователя") && !userDetails.getAuthorities().contains(new SimpleGrantedAuthority(UserRole.ROLE_CUSTOMER.name()))){
                throw new IllegalAccessException("No granted role");
            }
            if(actName.contains("модератора") && !userDetails.getAuthorities().contains(new SimpleGrantedAuthority(UserRole.ROLE_MODERATOR.name()))){
                throw new IllegalAccessException("No granted role");
            }

            delegateExecution.setVariable("user", userDetails);
        } catch (Throwable throwable) {
            throw new BpmnError("login_error", throwable.getMessage());
        }
    }
}
