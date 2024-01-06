package ca.ulaval.glo4003.repul.subscription.domain.exception;

import java.util.Arrays;

import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;

public class InvalidGenderException extends SubscriptionException {
    public InvalidGenderException() {
        super("The gender must be one of the following: " + Arrays.toString(Gender.values()));
    }
}
