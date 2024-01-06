package ca.ulaval.glo4003.repul.commons.domain.exception;

public class InvalidIDULException extends CommonException {
    public InvalidIDULException() {
        super("The given IDUL is invalid.");
    }
}
