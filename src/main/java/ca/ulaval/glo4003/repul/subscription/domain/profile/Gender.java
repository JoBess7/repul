package ca.ulaval.glo4003.repul.subscription.domain.profile;

import ca.ulaval.glo4003.repul.subscription.domain.exception.InvalidGenderException;

public enum Gender {
    MAN,
    WOMAN,
    OTHER,
    UNDISCLOSED;

    public static Gender from(String gender) {
        try {
            return Gender.valueOf(gender);
        } catch (IllegalArgumentException e) {
            throw new InvalidGenderException();
        }
    }
}
