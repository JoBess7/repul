package ca.ulaval.glo4003.repul.user.domain.identitymanagment;

import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.InvalidPasswordException;

public record Password(
    String value
) {
    public Password {
        if (value.isBlank()) {
            throw new InvalidPasswordException();
        }
    }
}
