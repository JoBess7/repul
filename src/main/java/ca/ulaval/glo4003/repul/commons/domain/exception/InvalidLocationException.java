package ca.ulaval.glo4003.repul.commons.domain.exception;

public class InvalidLocationException extends CommonException {
    public InvalidLocationException() {
        super("The given location is invalid.");
    }
}
