package itmo.blps.dto;

import javax.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDetailsDTO {
    @Email(message = "Электронная почта недействительна")
    private String email;
    @Length(max = 10, message = "Слишком длинный пароль")
    @Length(min = 3, message = "Слишком короткий пароль")
    private String password;
}
