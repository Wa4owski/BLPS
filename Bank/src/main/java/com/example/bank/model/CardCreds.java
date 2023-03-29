package com.example.bank.model;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CardCreds {
    @Pattern(regexp = "\\d{4}-\\d{4}-\\d{4}-\\d{4}", message = "номер карты невалиден")
    private String number;
    @Length(min = 6)
    @Length(max = 20)
    private String password;
}
