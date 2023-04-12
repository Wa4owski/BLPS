package com.example.moneyman2.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanParamsDTO {
    @Max(value = 100000, message = "Максимальная сумма займа - 100000р")
    @Min(value = 1500, message = "Минимальная сумма займа - 1500р")
    private Integer sum;
    @Max(value = 7*18, message = "Максимальный срок возвращения займа - 18 недель")
    @Min(value = 5, message = "Минимальная срок возвращения займа - 5 дней")
    private Integer term;
}
