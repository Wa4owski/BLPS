package com.example.lab1.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CardDetails {
    @Pattern(regexp = "\\d{4}-\\d{4}-\\d{4}-\\d{4}")
    private String cardNumber;
    @Pattern(regexp = "\\d{3}")
    private String cvs;
    @Pattern(regexp = "\\d{2}-\\d{2}")
    private String expDate;
}
