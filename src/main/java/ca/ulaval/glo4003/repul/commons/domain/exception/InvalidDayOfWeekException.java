package ca.ulaval.glo4003.repul.commons.domain.exception;

public class InvalidDayOfWeekException extends CommonException {
    public InvalidDayOfWeekException() {
        super("The given day of week is invalid. Accepted values are : 'SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY' and 'SATURDAY'.");
    }
}
