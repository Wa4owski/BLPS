package itmo.blps.entity;

import javax.persistence.*;

import itmo.blps.dto.PassportDetailsDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "passport_details")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PassportDetailsEntity {
    @Id
    private Integer customerId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private UserDetailsEntity userDetails;

    //TODO add full_name
    private String address;
    private String series;
    private String number;

    public PassportDetailsEntity(PassportDetailsDTO passportDetailsDTO) {
        this.address = passportDetailsDTO.getAddress();
        this.series = passportDetailsDTO.getSeries();
        this.number = passportDetailsDTO.getNumber();
    }
}
