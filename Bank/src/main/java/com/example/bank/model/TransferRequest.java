package com.example.bank.model;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransferRequest implements Serializable {
    @Pattern(regexp = "\\d{4}-\\d{4}-\\d{4}-\\d{4}", message = "номер карты получателя невалиден")
    private String recipientCardNumber;
    @Pattern(regexp = "\\d{4}-\\d{4}-\\d{4}-\\d{4}", message = "номер карты отправителя невалиден")
    private String senderCardNumber;
    @Length(min = 6)
    @Length(max = 20)
    private String senderCardPassword;
    @Positive
    private Integer sum;
}
