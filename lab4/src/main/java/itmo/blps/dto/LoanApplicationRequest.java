package itmo.blps.dto;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoanApplicationRequest {
    @NonNull
    @Valid
    private LoanParamsDTO loanParamsDTO;

    @NotNull
    @Valid
    private CardCredentials cardCredentials;
}
