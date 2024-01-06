package ca.ulaval.glo4003.repul.commons.domain.exception;

public class InvalidLocationIdException extends CommonException {
    public InvalidLocationIdException() {
        super("The given location Id is invalid.");
    }
}
