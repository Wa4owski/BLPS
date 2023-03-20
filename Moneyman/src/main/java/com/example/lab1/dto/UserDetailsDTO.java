package com.example.lab1.dto;

import jakarta.validation.constraints.Email;
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
    @Length(max = 100, message = "Слишком длинное имя")
    @Length(min = 8, message = "Слишком короткое имя")
    private String fullName;
    @Email(message = "Электронная почта недействительна")
    private String email;
}
