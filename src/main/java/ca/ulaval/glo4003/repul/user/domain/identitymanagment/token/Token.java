package ca.ulaval.glo4003.repul.user.domain.identitymanagment.token;

import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.InvalidTokenException;

public record Token(
    String value,
    int expiresIn
) {
    public Token {
        if (value.isBlank() || expiresIn < 0) {
            throw new InvalidTokenException();
        }
    }
}
