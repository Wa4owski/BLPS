package itmo.blps.dto;

import javax.validation.constraints.Pattern;

import itmo.blps.entity.PassportDetailsEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PassportDetailsDTO {
//    @Length(max = 100, message = "Слишком длинное имя")
//    @Length(min = 8, message = "Слишком короткое имя")
//    private String fullName;
    @Length(max = 255, message = "Слишком длинный адресс")
    @Length(min = 10, message = "Слишком короткий адресс")
    private String address;
    @Pattern(regexp = "\\d{4}")
    private String series;
    @Pattern(regexp = "\\d{6}")
    private String number;

    public PassportDetailsDTO(PassportDetailsEntity passportDetails) {
        this.address = passportDetails.getAddress();
        this.number = passportDetails.getNumber();
        this.series = passportDetails.getSeries();
    }
}
