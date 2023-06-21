package itmo.blps.exception;

public class IllegalCardNumberException extends CardCredsException{
    public IllegalCardNumberException(Integer loanApplicationId, String message) {
        super(loanApplicationId, message);
        this.loanApplicationId = loanApplicationId;
    }
}
