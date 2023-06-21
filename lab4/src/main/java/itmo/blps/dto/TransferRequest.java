package itmo.blps.dto;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TransferRequest implements Serializable {
    private Integer approvedAppId;
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
