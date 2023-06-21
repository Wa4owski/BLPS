package itmo.blps.exception;

public class BankResponseException extends RuntimeException{
    public BankResponseException(String message) {
        super(message);
    }
}
