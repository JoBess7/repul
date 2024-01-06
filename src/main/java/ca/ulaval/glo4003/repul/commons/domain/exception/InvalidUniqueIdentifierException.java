package ca.ulaval.glo4003.repul.commons.domain.exception;

public class InvalidUniqueIdentifierException extends CommonException {
    public InvalidUniqueIdentifierException() {
        super("The id is invalid.");
    }
}
