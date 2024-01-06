package ca.ulaval.glo4003.repul.commons.domain.exception;

public class InvalidCardNumberException extends CommonException {
    public InvalidCardNumberException() {
        super("The card number must be 9 digits");
    }
}
