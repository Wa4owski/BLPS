package itmo.blps.dto;

import javax.validation.constraints.Pattern;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CardCredentials implements Serializable {
    @Pattern(regexp = "\\d{4}-\\d{4}-\\d{4}-\\d{4}")
    private String cardNumber;
    @Pattern(regexp = "\\d{3}")
    private String cvs;
    @Pattern(regexp = "\\d{2}-\\d{2}")
    private String expDate;
}
