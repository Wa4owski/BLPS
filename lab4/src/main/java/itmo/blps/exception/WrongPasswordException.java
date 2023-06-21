package itmo.blps.exception;

public class WrongPasswordException extends CardCredsException {
    public WrongPasswordException(Integer loanApplicationId, String message) {
        super(loanApplicationId, message);
        this.loanApplicationId = loanApplicationId;
    }
}
