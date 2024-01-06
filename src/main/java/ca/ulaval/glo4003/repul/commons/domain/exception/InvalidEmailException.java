package ca.ulaval.glo4003.repul.commons.domain.exception;

public class InvalidEmailException extends CommonException {
    public InvalidEmailException() {
        super("The given email is invalid.");
    }
}
