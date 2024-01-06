package ca.ulaval.glo4003.repul.commons.domain.exception;

public class InvalidAmountException extends CommonException {
    public InvalidAmountException() {
        super("Amount must be positive.");
    }
}
