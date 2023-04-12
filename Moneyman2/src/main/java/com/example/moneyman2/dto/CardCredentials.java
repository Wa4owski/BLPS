package com.example.moneyman2.dto;

import jakarta.validation.constraints.Pattern;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CardCredentials {
    @Pattern(regexp = "\\d{4}-\\d{4}-\\d{4}-\\d{4}")
    private String cardNumber;
    @Pattern(regexp = "\\d{3}")
    private String cvs;
    @Pattern(regexp = "\\d{2}-\\d{2}")
    private String expDate;
}
